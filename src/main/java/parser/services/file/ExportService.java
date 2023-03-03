package parser.services.file;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ExportService {
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @SneakyThrows
    public byte[] loadFile(String fileId) {
        URL url = new URL(fileInfoUri + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFile = br.readLine();
        JSONObject jsonObject = new JSONObject(getFile);
        JSONObject path = jsonObject.getJSONObject("result");
        String filePath = path.getString("file_path");
        try (InputStream is = new URL(fileStorageUri + filePath).openStream()) {
            br.close();
            return IOUtils.toByteArray(is);
        }
    }
}
