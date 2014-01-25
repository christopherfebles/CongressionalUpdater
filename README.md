CongressionalUpdater
====================

This project downloads Congressional data from Wikipedia and the Sunlight foundation, exports the data to XML and resizes photos for display on iPhone.

Photo Updating Instructions
---------------------------

* Start MySQL Server
* Run CongressionalUpdater
* Console output will indicate any new members downloaded
* Confirm photos auto-selected are correct for the new members
    * Manually correct photos in database if needed
        * To correct a photo in the database:
            * Delete the contents in the "photo" database column
            * Update the "photo_url" column to point to the correct photo
            * Re-run CongressionalUpdater, and it will download the new photo
* Stop MySQL Server
* Note which new members have been added in the change notes
* Update version number in Xcode
* Copy updated representatives.xml and senators.xml into Xcode project
	* representatives.xml will be especially large
* Copy newly generated photos into Xcode project's Photos subfolder
	* Copy in Finder
	* "Add Files to…" within Xcode
* Test new Xcode build to ensure new photos and any new data appear
	* Typically for new members, minimal data (other than name and bioguideId) is available
* Build and submit updated project to App Store
* Commit Xcode changes to Git