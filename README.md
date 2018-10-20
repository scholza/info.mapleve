# info.mapleve
Manufacturing Planner for Eve

# Installation
## Prerequisites
you need a Java 9 runtime

## Download & Run
Get the zip file from the "releases" tab. Unzip and launch the `start.bat` file. You cannot use the program yet, please follow the next step!

## Eve Databases
Maple-VE needs the SDE database provided by CCP. 

* Go to https://developers.eveonline.com/resource/resources and download the `sde-xyz.zip` file from the "Static Data Export" section. 
* Unzip
* Copy the files `blueprints.yaml` and `typeIDs.yaml` from the `sde/fsd/` folder of the zip file to the base directory of Maple-VE (the directory where the `start.bat` file is located
* Hit the reload icon on the top center of the application (behind the "SDE Database Status"); the program will try to find the files copied in the last step and show green checkmarks if successful. If an error occurs hover over the error sign for more details. Please note the type database is huge and it might take several seconds to load it.

# Use
Copy a list of items in the leftmost text area. Currently only the "fitting" format is supported, i.e., each item has to be in a separate row and it is not possible to enter quantities such as `2x Myrmidon`. However it is possible to enter multiple fittings at once.

Example

    [Myrmidon, Artillery Fit]
    720mm Howitzer Artillery II
    720mm Howitzer Artillery II
    720mm Howitzer Artillery II
    720mm Howitzer Artillery II
    
    [Myrmidon, Half Empty Fit]
    720mm Howitzer Artillery II
    720mm Howitzer Artillery II
    
    Myrmidon
    
will be aggregated into

    3x Myrmidon
    6x 720mm Howitzer Artillery II
    
Hit the reload icon above the leftmost text area to calculate the needed materials.

The center box will show a tree based breakdown of all needed materials

The right box will show the "components" needed for manufacturing, i.e., only the semi-finished materials (typically T1 modules)
