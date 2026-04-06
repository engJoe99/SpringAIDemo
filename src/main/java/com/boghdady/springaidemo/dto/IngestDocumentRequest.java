package com.boghdady.springaidemo.dto;

import java.util.List;

public record IngestDocumentRequest(
        List<DocumentEntry> entries
) { }
