# Overview
A class for the save retrieval of settings.

It loads a settings file, creating one if none exists, and is able to give safeAccess to the values.

SafeAccess means if it is unable to read a property, it will delete the setting file and regenerate a default settings file, ensuring safe access even when the user manipulated the file by hand.