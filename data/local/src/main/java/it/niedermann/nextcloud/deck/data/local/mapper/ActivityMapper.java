package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.ActivityEntity;
import it.niedermann.nextcloud.deck.domain.model.Activity;

@Mapper(uses = {CommonLocalMapper.class})
public interface ActivityMapper extends GenericMapper<ActivityEntity, Activity> {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "etag", ignore = true)
    ActivityEntity toEntity(Activity activity);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(entity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "author", ignore = true)
    Activity toTO(ActivityEntity entity);
}
