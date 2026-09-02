package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity;
import it.niedermann.nextcloud.deck.data.shared.Attachment;

@Mapper(uses = {CommonLocalMapper.class})
public interface AttachmentMapper extends GenericMapper<AttachmentEntity, Attachment> {

    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "filesize", source = "size")
    AttachmentEntity toEntity(Attachment attachment);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "title", source = "data")
    @Mapping(target = "size", source = "filesize")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "localPath", source = "localPath")
    Attachment toTO(AttachmentEntity entity);

    @Mapping(target = "id", source = "localId")
    @Mapping(target = "title", source = "data")
    @Mapping(target = "size", source = "filesize")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "localPath", source = "localPath")
    it.niedermann.nextcloud.deck.domain.model.query.Attachment toQueryTO(AttachmentEntity entity);

    List<it.niedermann.nextcloud.deck.domain.model.query.Attachment> toQueryTOList(List<AttachmentEntity> entities);
}
