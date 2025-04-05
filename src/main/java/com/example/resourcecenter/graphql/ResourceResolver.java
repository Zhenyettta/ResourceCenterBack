package com.example.resourcecenter.graphql;

import com.example.resourcecenter.dto.ResourceDto;
import com.example.resourcecenter.entity.User;
import com.example.resourcecenter.service.ResourceService;
import com.example.resourcecenter.util.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ResourceResolver {

    private final ResourceService resourceService;

    @QueryMapping
    public List<ResourceDto> getResources() {
        return resourceService.getAllActive()
                .stream()
                .map(ResourceMapper::toDto)
                .collect(Collectors.toList());
    }

    @QueryMapping(name = "resource")
    public ResourceDto resource(@Argument Long id, @AuthenticationPrincipal User user) {
        return ResourceMapper.toDto(resourceService.getById(id, user));
    }


    @MutationMapping
    public ResourceDto toggleResource(@Argument Long id) {
        return ResourceMapper.toDto(resourceService.toggleActive(id));
    }

    @MutationMapping
    public Boolean deleteResource(@Argument Long id) {
        resourceService.delete(id);
        return true;
    }

    @QueryMapping
    public List<ResourceDto> content(
            @Argument int page,
            @Argument String search,
            @Argument String sort,
            @Argument boolean activeOnly,
            @Argument int size
    ) {
        if(Objects.equals(sort, "top")) {
            sort = "averageRating";
        }
        return resourceService.search(search, page, size, sort, "desc", activeOnly)
                .map(ResourceMapper::toDto)
                .toList();
    }
}
