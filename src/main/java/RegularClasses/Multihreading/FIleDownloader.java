package RegularClasses.Multihreading;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class FIleDownloader extends Task {

    private static final String DANE_HYDROLOGICZNE_DOBOWE = "https://danepubliczne.imgw.pl/data/dane_pomiarowo_obserwacyjne/dane_hydrologiczne/dobowe/";
    public static final String PATHNAME = "downloaded_files/";
    public static List<String> errorList = new ArrayList<>();
    public static Task myTask;

    private static boolean test(File file) {
        return file.getName().matches("(.*)\\.(zip)");
    }

    @Override
    public Void call() {
        download();
        return null;
    }

    public void download() {
        myTask = this;
        String beforeMonth;

        for (int year = 2019; year < Year.now().getValue(); year++) {
            if (isCancelled()) break;
            for (int month = 1; month < 13; month++) {
                if (isCancelled()) break;
                System.out.println(year + " " + month);
                beforeMonth = month < 10 ? "0" : "";
                StringBuilder fileName = new StringBuilder();
                fileName.append("codz_");
                fileName.append(year);
                fileName.append("_");
                fileName.append(beforeMonth);
                fileName.append(month);
                fileName.append(".zip");
                if (Files.exists(Paths.get(PATHNAME + fileName.toString()))) {
                    continue;
                }
                URL url = null;
                try {
                    StringBuilder urlAppender = new StringBuilder();
                    urlAppender.append(DANE_HYDROLOGICZNE_DOBOWE);
                    urlAppender.append(year);
                    urlAppender.append(System.getProperty("file.separator"));
                    urlAppender.append(fileName);
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
                    int bytes = 0;
                    while ((bytes = bufferedInputStream.read()) != -1) {
                        outputStream.write(bytes);
                    }
                    downloadStream.close();
                    bufferedInputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    if (url != null) errorList.add(e.getCause() + " link: " + url.toString());
                    else errorList.add(e.getCause() + " link: " + "is null");
                }
            }
        }
        File zippedFilesDirectory = new File(PATHNAME);
        List<File> zippedFileList = Arrays.stream(zippedFilesDirectory.listFiles()).filter(FIleDownloader::test).collect(Collectors.toList());
        zippedFileList.forEach(file -> {
            ZipInputStream is = null;
            OutputStream os = null;
            try {
                ZipFile zipFile = new ZipFile(file);
                // Get a list of FileHeader. FileHeader is the header information
                // for all the files in the ZipFile
                List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
                // Loop through all the fileHeaders
                for (FileHeader fileHeader : fileHeaderList) {
                    if (fileHeader != null) {
                        // Build the output file
                        String outFilePath = PATHNAME
                                + System.getProperty("file.separator")
                                + fileHeader.getFileName();
                        File outFile = new File(outFilePath);
                        // Get the InputStream from the ZipFile
                        is = zipFile.getInputStream(fileHeader);
                        // Initialize the output stream
                        os = new FileOutputStream(outFile);
                        int readLen;
                        byte[] buff = new byte[4096];
                        // Loop until End of File and write the contents to the
                        // output stream
                        while ((readLen = is.read(buff)) != -1) {
                            os.write(buff, 0, readLen);
                        }

                        closeFileHandlers(is, os);
                    } else {
                        errorList.add("plik uszkodzony lub pusty: " + file.getName());
                    }
                }
            } catch (Exception e) {
                errorList.add(e.getMessage() + " plik: " + file.getName());
            } finally {
                try {
                    closeFileHandlers(is, os);
                } catch (IOException e) {
                    errorList.add(e.getMessage() + "plik: " + file.getName());
                }
            }
        });
        showAlertWindow();
    }

    private void showAlertWindow() {
        if (!errorList.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                TextArea textArea = new TextArea();
                errorList.forEach(line -> textArea.setText(textArea.getText() + line + "\n"));
                alert.getDialogPane().setContent(textArea);
            });
        }
    }

    private void closeFileHandlers(ZipInputStream is, OutputStream os)
            throws IOException {
        if (os != null) os.close();
        if (is != null) is.close();
    }
}
