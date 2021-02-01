package RegularClasses.Multihreading;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class FIleDownloader extends Task<Void> {

    private static final String DANE_HYDROLOGICZNE_DOBOWE = "https://danepubliczne.imgw.pl/data/dane_pomiarowo_obserwacyjne/dane_hydrologiczne/dobowe/";
    public static String PATHNAME;
    public static List<String> errorList = new ArrayList<>();
    public static Task<Void> myTask;

    private static Logger logger = LogManager.getLogger(FIleDownloader.class);

    @Override
    public Void call() {
        myTask = this;
        String beforeMonth;
        updateMessage("Pobieranie");
        for (int year = 2000; year < Year.now().getValue(); year++) {
            if (isCancelled()) break;
            for (int month = 1; month < 13; month++) {
                if (isCancelled()) break;
                logger.info(year + " " + month);
                beforeMonth = month < 10 ? "0" : "";
                StringBuilder fileName = new StringBuilder();
                fileName.append("codz_");
                fileName.append(year);
                fileName.append("_");
                fileName.append(beforeMonth);
                fileName.append(month);
                fileName.append(".zip");
                if (Files.exists(Paths.get(PATHNAME + fileName.toString()))) continue;
                URL url = null;
                try {
                    StringBuilder urlAppender = new StringBuilder();
                    urlAppender.append(DANE_HYDROLOGICZNE_DOBOWE);
                    urlAppender.append(year);
                    urlAppender.append("/");
                    urlAppender.append(fileName);
                    updateMessage("Pobieram plik z roku: " + year + " miesi¹c: " + month);
                    url = new URL(urlAppender.toString());
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    int statusCode = httpConnection.getResponseCode(); //get response code
                    if (statusCode != HttpURLConnection.HTTP_OK) { // if file downloadStream moved, then pick new URL
                        String returnedUrl = httpConnection.getHeaderField("Location");
                        url = new URL(returnedUrl);
                        httpConnection = (HttpURLConnection) url.openConnection();
                    }
                    InputStream downloadStream = httpConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(downloadStream);
                    FileOutputStream outputStream = new FileOutputStream(PATHNAME + fileName);
                    int bytes;
                    while ((bytes = bufferedInputStream.read()) != -1) {
                        outputStream.write(bytes);
                    }
                    downloadStream.close();
                    bufferedInputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    if (url != null) {
                        errorList.add(e.getCause() + " link: " + url.toString());
                        logger.warn("error during file downloading: " + e.getCause() + " " + e.getMessage());
                    } else {
                        errorList.add(e.getCause() + " link: " + "is null");
                    }
                }
            }
        }
        File zippedFilesDirectory = new File(PATHNAME);
        List<File> zippedFileList = Arrays.stream(Objects.requireNonNull(zippedFilesDirectory.listFiles())).filter(FIleDownloader::test).collect(Collectors.toList());
        updateMessage("Rozpakowywanie");
        zippedFileList.forEach(file -> {
            ZipInputStream is = null;
            OutputStream os = null;
            try {
                ZipFile zipFile = new ZipFile(file);
                List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
                for (FileHeader fileHeader : fileHeaderList) {
                    if (fileHeader != null) {
                        String outFilePath = PATHNAME
                                + System.getProperty("file.separator")
                                + fileHeader.getFileName();
                        File outFile = new File(outFilePath);
                        is = zipFile.getInputStream(fileHeader);
                        os = new FileOutputStream(outFile);
                        int readLen;
                        byte[] buff = new byte[4096];
                        while ((readLen = is.read(buff)) != -1) {
                            os.write(buff, 0, readLen);
                        }
                        closeFileHandlers(is, os);
                        updateMessage("Rozpakowujê " + file.getName());
                    } else {
                        errorList.add("plik uszkodzony lub pusty: " + file.getName());
                    }
                }
            } catch (Exception e) {
                errorList.add(e.getMessage() + " plik: " + file.getName());
//                logger.warning("cos sie sta³o!");
            } finally {
                try {
                    closeFileHandlers(is, os);
                } catch (IOException e) {
                    errorList.add(e.getMessage() + "plik: " + file.getName());
//                    logger.warning("cos sie sta³o!");
                }
            }
        });
        showAlertWindow();
        myTask = null;
        return null;
    }

    private void showAlertWindow() {
        if (!errorList.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                TextArea textArea = new TextArea();
                errorList.forEach(line -> textArea.setText(textArea.getText() + line + "\n"));
                alert.getDialogPane().setContent(textArea);
                alert.showAndWait();
            });
        }
    }

    private void closeFileHandlers(ZipInputStream is, OutputStream os)
            throws IOException {
        if (os != null) os.close();
        if (is != null) is.close();
    }
    private static boolean test(File file) {
        return file.getName().matches("(.*)\\.(zip)");
    }

}
