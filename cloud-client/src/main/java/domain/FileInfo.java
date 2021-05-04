package domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {
    public static final String UP_TOKEN = "[..]";

    private String filename;
    private long length;
    private String fileType;
    private File pathToFile;


    private static final long GigaByte = 1073741824L;
    private static final long MegaByte = 1048576L;
    private static final long KiloByte = 1024L;
    private static final String GB = "Gb";
    private static final String MB = "Mb";
    private static final String KB = "Kb";
    private static final String BYTE = "byte";


    public FileInfo (Path path){
        try {
            this.filename = path.getFileName().toString();
            if (Files.isDirectory(path)) {
                this.length = -1L;
            } else {

                this.length = Files.size(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Something wrong with file: " +path.toAbsolutePath().toString());
        }
    }
    public FileInfo(String filename, long length, File pathToFile){
        this.filename = filename;
        this.length = length;
        this.pathToFile = pathToFile;
    }


    public boolean isDirectory() {
        return length == -1L;
    }

    public boolean isUpElement() {
        return length == -2L;
    }

    public FileInfo(String filename, long length) {
        this.filename = filename;
        this.length = length;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize (long length) {

        if (length / GigaByte > 0) {
            filename = GB;
            return length / GigaByte;
        } if (length / MegaByte > 0) {
            fileType = MB;
            return length / MegaByte;
        } if (length / KiloByte > 0) {
            fileType = KB;
            return length / KiloByte;
        } else {
            fileType = BYTE;
            return length;
        }
    }
}
