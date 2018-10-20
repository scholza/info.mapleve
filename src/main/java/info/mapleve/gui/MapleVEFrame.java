package info.mapleve.gui;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.tbee.javafx.scene.layout.MigPane;

import info.mapleve.bom.BOM;
import info.mapleve.bom.BOMs;
import info.mapleve.sde.Blueprints;
import info.mapleve.sde.Types;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jiconfont.icons.FontAwesome;
import jiconfont.javafx.IconFontFX;


public class MapleVEFrame extends Application {

	private Label lblTypesStatus;
	private Label lblBlueprintStatus;
	private TextArea txtFitting; 
	private TextArea txtBreakdown; 
	private TextArea txtComponents; 
	
	private Optional<Types> types = Optional.empty();
	private Optional<Blueprints> blueprints = Optional.empty();
	
	
	private final ExecutorService loader = Executors.newFixedThreadPool(2, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
            return t;
		}
	});
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		IconFontFX.register(FontAwesome.getIconFont());
		
		MigPane root = new MigPane("fill", "[grow]", "[]20[grow]");
		
        MigPane sde = new MigPane();
        root.add(sde, "grow 1 0, wrap");
        HBox sdeHeader = new HBox();
		sde.add(sdeHeader, "span, wrap");
		
		sdeHeader.getChildren().add(new Label("SDE Database Status (Static Data Export files from EVE)"));
        Hyperlink linkRefreshBlueprint = new Hyperlink("", 
        		new ImageView(IconFontFX.buildImage(FontAwesome.REFRESH, 16, Color.BLACK)));
        linkRefreshBlueprint.onActionProperty().set(this::onRefreshSDE);
        sdeHeader.getChildren().add(linkRefreshBlueprint);
		
        sde.add(new Label("Types (typeIDs.yaml):"), "gap indent");
        lblTypesStatus = new Label("");
		sde.add(lblTypesStatus, "gap related");
		
		sde.add(new Label("      Blueprints (blueprints.yaml):"), "gap unrelated");
        lblBlueprintStatus = new Label("");
		sde.add(lblBlueprintStatus, "gap related");
		
		
		
		MigPane planner = new MigPane("fill", "10[33%,fill]20[33%,fill]20[33%,fill]", "[][grow]");
        root.add(planner, "grow 1 1");
		
        
        HBox fitting = new HBox();
        fitting.getChildren().add(new Label("Fitting / Products"));
        Hyperlink linkRefreshFitting = new Hyperlink("", 
        		new ImageView(IconFontFX.buildImage(FontAwesome.REFRESH, 16, Color.BLACK)));
        linkRefreshFitting.onActionProperty().set(this::onRecalc);
        fitting.getChildren().add(linkRefreshFitting);
        
        planner.add(fitting);
        planner.add(new Label("Breakdown"));
        planner.add(new Label("Components"), "wrap");
		
        
        Font font = new Font("Consolas", 14);
        
        txtFitting = new TextArea("");
        txtFitting.setFont(font);
		planner.add(txtFitting, "grow");
		
		txtBreakdown = new TextArea("");
		txtBreakdown.setEditable(false);
		txtBreakdown.setFont(font);
        planner.add(txtBreakdown, "grow");
        
        txtComponents = new TextArea("");
        txtComponents.setFont(font);
        txtComponents.setEditable(false);
        planner.add(txtComponents, "grow");
        
		
        primaryStage.setTitle("Maple-VE");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
        
        onRefreshSDE(null);
	}
	
	
	public void onRecalc(ActionEvent ae) {
		txtBreakdown.setText("");
		txtComponents.setText("");
		
		if (!types.isPresent() || !blueprints.isPresent()) {
			return;
		}
		
		String fitting = txtFitting.getText();

		BOMs boms = new BOMs(types.get(), blueprints.get());
		BOM bom = boms.parse(fitting).aggregateLineItems();
		boms.calculateMaterials(bom);
		
		txtBreakdown.setText(bom.toString());
		txtComponents.setText(bom.extractComponents().aggregateLineItems().toString());
	}
	
	
	public void onRefreshSDE(ActionEvent ae) {
		loader.execute(() -> {
			Platform.runLater(() -> {
				lblTypesStatus.setGraphic(new ImageView(
						IconFontFX.buildImage(FontAwesome.HOURGLASS, 16, Color.BLACK)));
			});
			
			try {
				types = Optional.of(Types.loadFromYaml());
			} 
			catch (IOException e) {
				Platform.runLater(() -> {
					lblTypesStatus.setGraphic(new ImageView(
							IconFontFX.buildImage(FontAwesome.EXCLAMATION_TRIANGLE, 16, Color.RED)));
					lblTypesStatus.setTooltip(new Tooltip(e.getMessage()));
					
					lblBlueprintStatus.setGraphic(new ImageView(
							IconFontFX.buildImage(FontAwesome.EXCLAMATION_TRIANGLE, 16, Color.RED)));
					lblBlueprintStatus.setTooltip(new Tooltip("types must be loaded before blueprints"));
				});
				
				return;
			}
			
			Platform.runLater(() -> {
				lblTypesStatus.setGraphic(new ImageView(
						IconFontFX.buildImage(FontAwesome.CHECK, 16, Color.GREEN)));
			});
			
			try {
				Blueprints bps = new Blueprints(types.get());
				bps.loadFromYaml();
				
				blueprints = Optional.of(bps);
			} 
			catch (IOException e) {
				Platform.runLater(() -> {
					lblTypesStatus.setGraphic(new ImageView(
							IconFontFX.buildImage(FontAwesome.EXCLAMATION_TRIANGLE, 16, Color.RED)));
					lblTypesStatus.setTooltip(new Tooltip(e.getMessage()));
				});
			}
			
			Platform.runLater(() -> {
				lblBlueprintStatus.setGraphic(new ImageView(
						IconFontFX.buildImage(FontAwesome.CHECK, 16, Color.GREEN)));
			});
		});
	}
}
