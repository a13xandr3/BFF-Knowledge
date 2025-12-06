package br.com.teixeiraesteves.bff.dto;

import java.util.List;

public record DeleteCompositeRequest(Long linkId, List<Long> fileIds) {}
