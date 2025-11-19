package com.phucchinh.dogomynghe.Mapper;
/**
 * Interface chung cho các mapper
 *
 * @param <D> Response DTO type
 * @param <E> Entity type
 */
public interface EntityMapper< D ,E > {
    /**
     * Chuyển đổi từ Entity sang Response DTO
     */
    D toDTO(E entity);
}
