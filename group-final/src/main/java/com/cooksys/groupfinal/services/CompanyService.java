package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.*;

import java.util.Set;

public interface CompanyService {

	Set<FullUserDto> getAllUsers(Long id);

	Set<AnnouncementDto> getAllAnnouncements(Long id);

	Set<TeamDto> getAllTeams(Long id);

	Set<ProjectDto> getAllProjects(Long companyId, Long teamId);


	BasicUserDto createUser(Long id, UserRequestDto userRequestDto);
	AnnouncementDto createAnnouncement(AnnouncementRequestDto announcementRequestDto, Long id);

	void deleteUser(Long id, Long userId);
}
