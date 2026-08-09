package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity;
import it.niedermann.nextcloud.deck.domain.model.Attachment;

@Mapper(uses = {CommonLocalMapper.class})
public interface AttachmentMapper extends GenericMapper<AttachmentEntity, Attachment> {

    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    @Override
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "etag", ignore = true)
    AttachmentEntity toEntity(Attachment attachment);

    @Override
    @Mapping(target = "id", source = "remoteId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "localPath", ignore = true)
    Attachment toTO(AttachmentEntity entity);
}
