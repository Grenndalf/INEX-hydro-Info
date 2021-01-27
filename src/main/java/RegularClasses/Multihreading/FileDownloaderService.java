package RegularClasses.Multihreading;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FileDownloaderService extends Service {
    @Override
    protected Task createTask() {
        return new FIleDownloader();
    }
}
