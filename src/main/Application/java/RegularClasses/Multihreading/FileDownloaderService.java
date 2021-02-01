package RegularClasses.Multihreading;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FileDownloaderService extends Service<Void> {
    @Override
    protected Task<Void> createTask() {
        return new FIleDownloader();
    }
}
