package com.cooksys.groupfinal.services.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.TeamMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

	private final TeamRepository teamRepository;
	private final TeamMapper teamMapper;
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;

	@Override
	public Set<TeamDto> getTeams(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (optionalUser.isEmpty()) {
			throw new NotFoundException("no user with id: " + id);
		}
		User user = optionalUser.get();
		return teamMapper.entitiesToDtos(user.getTeams());
	}

	@Override
	public TeamDto createTeam(Long authorId, Long companyId, TeamDto teamDto) {

		Optional<User> optionalUser = userRepository.findById(authorId);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("user with id: " + authorId + " not found.");
		}

		User user = optionalUser.get();

		if (!user.isAdmin()) {
			throw new NotAuthorizedException("You are not authorized to create a team.");
		}

		Team createdTeam = teamMapper.dtoToEntity(teamDto);
		Optional<Company> optionalCompany = companyRepository.findById(companyId);
		if (optionalCompany.isEmpty()) {
			throw new NotFoundException("company with id: " + companyId + " not found.");
		}

		Company company = optionalCompany.get();

		createdTeam.setCompany(company);
		Team savedTeam = teamRepository.saveAndFlush(createdTeam);

		return teamMapper.entityToDto(savedTeam);
	}

	@Override
	public TeamDto deleteTeam(Long id) {
		Optional<Team> teamToDelete = teamRepository.findById(id);
		if (teamToDelete.isEmpty()) {
			throw new NotFoundException("team with id: " + id + " not found.");
		}

		Team team = teamToDelete.get();
		team.setDeleted(true);
		return teamMapper.entityToDto(teamRepository.saveAndFlush(team));
	}

	
	public TeamDto getTeamById(Long teamId) {
		Optional<Team> foundTeam = teamRepository.findById(teamId);
		if (foundTeam.isEmpty()) {
			throw new NotFoundException("team with id: " + teamId + " not found.");
		}

		return teamMapper.entityToDto(foundTeam.get());
	}
}
