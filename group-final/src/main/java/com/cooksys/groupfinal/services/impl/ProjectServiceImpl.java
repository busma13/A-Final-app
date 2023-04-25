package com.cooksys.groupfinal.services.impl;

import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.services.ProjectService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final TeamRepository teamRepository;

    private final ObjectMapper objectMapper;


    @Override
    public ProjectDto createProject(Map<String, Object> json) {
        BasicUserDto basicUserDto = objectMapper.convertValue(json.get("user"), new TypeReference<BasicUserDto>() {});
        ProjectDto projectDto = objectMapper.convertValue(json.get("project"), new TypeReference<ProjectDto>() {});

        if(!basicUserDto.isAdmin()){
            throw new NotAuthorizedException("You are not authorized to do this action.");
        }

        if (projectDto.getTeam() == null || projectDto.getTeam().getId() == null){
            throw new BadRequestException("The given team must not be null");
        }

        if(teamRepository.findById(projectDto.getTeam().getId()).isPresent()){
            Team team = teamRepository.findById(projectDto.getTeam().getId()).get();

            Project project = projectRepository.saveAndFlush(projectMapper.dtoToEntity(projectDto));

            Set<Project> teamProjects = team.getProjects();
            teamProjects.add(project);
            teamRepository.saveAndFlush(team);

            return projectMapper.entityToDto(projectRepository.saveAndFlush(project));
        } else {
            throw new NotFoundException("No Team exists with this id: " + projectDto.getTeam().getId());
        }
    }


//    public ProjectDto createProject(ProjectDto projectDto) {
//
//        if (projectDto.getTeam() == null || projectDto.getTeam().getId() == null){
//            throw new BadRequestException("The given team must not be null");
//        }
//
//        if(teamRepository.findById(projectDto.getTeam().getId()).isPresent()){
//            Team team = teamRepository.findById(projectDto.getTeam().getId()).get();
//
//            Project project = projectRepository.saveAndFlush(projectMapper.dtoToEntity(projectDto));
//
//            Set<Project> teamProjects = team.getProjects();
//            teamProjects.add(project);
//            teamRepository.saveAndFlush(team);
//
//            return projectMapper.entityToDto(projectRepository.saveAndFlush(project));
//        } else {
//            throw new NotFoundException("No Team exists with this id: " + projectDto.getTeam().getId());
//        }
//
//    }

    @Override
    public void deleteProject(Long projectId) {

        if (projectRepository.findById(projectId).isPresent()){
            Project projectToDelete = projectRepository.findById(projectId).get();

            Team team = projectToDelete.getTeam();
            team.getProjects().remove(projectToDelete);

            projectToDelete.setDeleted(true);
            projectToDelete.setActive(false);
            projectToDelete.setTeam(null);

            teamRepository.saveAndFlush(team);

        } else {
            throw new NotFoundException("No Project exists with this id: " + projectId);
        }
    }

    @Override
    public ProjectDto getProjectByProjectId(Long projectId) {

        Optional<Project> projectToFind = projectRepository.findById(projectId);

        if (projectToFind.isEmpty()){
            throw new NotFoundException("No project exists with this id: " + projectId);
        } else if (projectToFind.get().isDeleted()){
            throw new NotFoundException("No project exists with this id: " + projectId);
        }else {
            return projectMapper.entityToDto(projectRepository.findById(projectId).get());
        }
    }

    //@Override
//    public List<ProjectDto> getAllProjectsByTeam() {
//        List<Team> allTeams = teamRepository.findAll();
//        List<Project> allProjects = new ArrayList<>();
//
//        for(Team team : allTeams){
//
//            allProjects.add();
//
//        }
//    }

}
