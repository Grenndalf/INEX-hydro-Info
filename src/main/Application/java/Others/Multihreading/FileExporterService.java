package Others.Multihreading;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FileExporterService extends Service {
    FileExporter fileExporter;

    public FileExporterService (FileExporter fileExporter) {
        this.fileExporter = fileExporter;
    }

    @Override
    protected Task createTask () {
        return fileExporter;
    }
}
