import { Navigate, redirect } from 'react-router-dom'
import { useRecoilState } from 'recoil'
import NavBar from '../../Components/NavBar'
import { userState, companyState, currentTeamState } from '../../globalstate'
import { Button, Card, CardContent, CardHeader } from '@mui/material'
import CreateTeamOverlay from '../../Components/CreateTeamOverlay'
import { useEffect, useState } from 'react'
import { getProjectsByTeam, getTeamById } from '../../Services/teams'

const cardStyle = {
  width: '30%',
  height: '300px',
}

const cardHeaderStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  margin: '1rem',
}

const cardContentStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  // .MuiCardHeader-content
}

const teammateStyle = {
  display: 'flex',
  flexWrap: 'wrap',
  gap: '1rem',
  justifyContent: 'space-between',
  alignItems: 'space-evenly',
}

const newTeamCardContentStyle = {
  height: '90%',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'space-evenly',
  alignItems: 'center',
}

const userBtnStyle = {
  width: '45%',
}

const Teams = () => {
  const [user, setUser] = useRecoilState(userState)
  const [company, setCompany] = useRecoilState(companyState)
  const [currentTeam, setCurrentTeam] = useRecoilState(currentTeamState)
  const [teamSelected, setTeamSelected] = useState(false)
  const [teamProjectsCounts, setTeamProjectsCounts] = useState([])

  useEffect(() => {
    const teamIds = company?.teams?.map((team) => team.id)
    teamIds?.forEach(async (teamId) => {
      const projects = await getProjectsByTeam(company.id, teamId)
      setTeamProjectsCounts([
        ...teamProjectsCounts,
        { teamId: teamId, projectsCount: '# of Projects: ' + projects.length },
      ])
    })
  }, [company])

  const handleCardClick = async (event) => {
    setCurrentTeam(await getTeamById(event.currentTarget.dataset.id))
    setTeamSelected(true)
  }

  const handleCreateNewTeam = () => {
    console.log('Add new team')
  }

  console.log(company)

  if (!user.isLoggedIn) {
    return <Navigate replace to='/' />
  } else if (company.length === 0) {
    return <Navigate replace to='/company' />
  } else {
    return teamSelected ? (
      <Navigate replace to='/projects' />
    ) : (
      <>
        <NavBar />
        <div
          style={{
            height: '94vh',
            width: '100%',
            display: 'flex',
            flexDirection: 'column',
            flexWrap: 'wrap',
            justifyContent: 'flex-start',
            alignItems: 'center',
          }}
        >
          <h1 style={{ marginTop: '6vh', color: '#1ba098' }}>Teams</h1>
          <div
            style={{
              width: '80%',
              display: 'flex',
              flexWrap: 'wrap',
              justifyContent: 'space-between',
              alignItems: 'space-evenly',
              rowGap: '5rem',
            }}
          >
            {company?.teams?.map((team) => (
              <Card
                key={team.id}
                data-id={team.id}
                onClick={handleCardClick}
                style={cardStyle}
              >
                <div style={cardHeaderStyle}>
                  <h2>{team.name}</h2>
                  <span>{teamProjectsCounts[0]?.projectsCount}</span>
                </div>
                <CardContent style={cardContentStyle}>
                  <h3>Members</h3>
                  <div style={teammateStyle}>
                    {team?.teammates?.map((user) => (
                      <Button
                        key={user.id}
                        variant='contained'
                        style={userBtnStyle}
                      >
                        {user?.profile?.firstName}{' '}
                        {user?.profile?.lastName?.slice(0, 1)}.
                      </Button>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
            <Card onClick={handleCreateNewTeam} style={cardStyle}>
              <CardContent style={newTeamCardContentStyle}>
                <CreateTeamOverlay></CreateTeamOverlay>
                <span>New Team</span>
              </CardContent>
            </Card>
          </div>
        </div>
      </>
    )
  }
}

export default Teams