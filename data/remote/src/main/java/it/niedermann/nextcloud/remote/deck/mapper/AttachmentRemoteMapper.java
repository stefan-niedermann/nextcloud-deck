package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.remote.deck.dto.AttachmentDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface AttachmentRemoteMapper extends GenericRemoteMapper<AttachmentDTO, Attachment> {

    AttachmentRemoteMapper INSTANCE = Mappers.getMapper(AttachmentRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cardId", source = "cardId")
    @Mapping(target = "extendedData.filesize", source = "size")
    @Mapping(target = "extendedData.mimetype", source = "mimetype")
    @Mapping(target = "extendedData.fileid", source = "fileId")
    @Mapping(target = "extendedData.info.dirname", source = "dirname")
    @Mapping(target = "extendedData.info.basename", source = "basename")
    @Mapping(target = "extendedData.info.extension", source = "extension")
    @Mapping(target = "extendedData.info.filename", source = "filename")
    @Mapping(target = "data", source = "title")
    @Mapping(target = "etag", ignore = true)
    AttachmentDTO toDTO(Attachment attachment);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cardId", source = "cardId")
    @Mapping(target = "size", source = "extendedData.filesize")
    @Mapping(target = "mimetype", source = "extendedData.mimetype")
    @Mapping(target = "fileId", source = "extendedData.fileid")
    @Mapping(target = "dirname", source = "extendedData.info.dirname")
    @Mapping(target = "basename", source = "extendedData.info.basename")
    @Mapping(target = "extension", source = "extendedData.info.extension")
    @Mapping(target = "filename", source = "extendedData.info.filename")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "title", source = "data")
    @Mapping(target = "localPath", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    Attachment toTO(AttachmentDTO attachmentDTO);
}
