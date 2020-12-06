package RegularClasses.Multihreading;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

public class FileImporterService extends Service<Void> {

    private final List<File> fileList;

    public FileImporterService(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    protected Task createTask() {
        FileImporter fileImporter = new FileImporter();
        fileImporter.setFileList(fileList);
        return fileImporter;
    }
}
