package de.devland.masterpassword.export;

/**
 * Created by deekay on 22/10/14.
 */
public enum ExportType {
    JSON, MPSITES;

    public String getFileExtension() {
        return this.toString().toLowerCase();
    }
}
