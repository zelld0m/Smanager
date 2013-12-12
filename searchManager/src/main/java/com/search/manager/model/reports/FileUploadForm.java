package com.search.manager.model.reports;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 16, 2013
 * @version 1.0
 */
public class FileUploadForm {

    private List<MultipartFile> files = new ArrayList<MultipartFile>();

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("files", files).
                toString();
    }
    
    
}
