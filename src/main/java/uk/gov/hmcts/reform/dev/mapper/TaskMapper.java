package uk.gov.hmcts.reform.dev.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.hmcts.reform.dev.entity.Task;
import uk.gov.hmcts.reform.dev.model.CreatedTaskModel;
import uk.gov.hmcts.reform.dev.model.TaskModel;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "systemId", ignore = true)
    @Mapping(target = "id", ignore = true)
    Task map(TaskModel model);

    @Mapping(target = "systemId", ignore = true)
    @Mapping(target = "id", ignore = true)
    Task map(TaskModel model, @MappingTarget Task entity);

    CreatedTaskModel map(Task entity);

    List<CreatedTaskModel> map(List<Task> tasks);
}
