package RegularClasses;

import DButils.DbActions;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class FileImporter extends Task {

    public static final String IN_FILE = "in.file";
    public static final String BUNDLES_LABELS = "Bundles.labels";
    public static Task myTask;
    public static List<String> importErrors = new ArrayList<>();

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    private List<File> fileList;
    private final DbActions dbActions = new DbActions();

    @Override
    protected Void call() {
        myTask = this;
        importErrors.clear();
        int progress = 0;
        for (File file : this.fileList
        ) {
            List<GaugeMeasurement> gaugeMeasurementsListFromOneFile = new ArrayList<>();

            try {
                Scanner scanner = new Scanner(new FileReader(file));
                while (scanner.hasNextLine()) {
                    String lineToAdd = scanner.nextLine();
                    GaugeMeasurement gaugeMeasurement = new GaugeMeasurement();
                    try {
                        gaugeMeasurement.tryGetGaugeMeasurement(lineToAdd.split(","));
                        gaugeMeasurementsListFromOneFile.add(gaugeMeasurement);
                    } catch (Exception e) {
                        importErrors.add(lineToAdd
                                + ResourceBundle.getBundle(BUNDLES_LABELS).getString(IN_FILE)
                                + file.getName()
                                + "\n");
                    }
                }

                if (isCancelled()) break;

                dbActions.createOrUpdate(gaugeMeasurementsListFromOneFile);

                progress++;
                updateProgress(progress, this.fileList.size());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        myTask = null;
        return null;
    }
}
