package uk.org.pentlandscouts.events.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.repositories.EventAttendeeHistRepository;
import uk.org.pentlandscouts.events.repositories.EventAttendeeRepository;

import java.util.List;

@Service
public class EventAttendeeHistService {

    private static final Logger logger = LoggerFactory.getLogger(EventAttendeeHistService.class);

    @Autowired
    EventAttendeeHistRepository repo;

    public List<EventAttendeeHist> findAll()
    {
        logger.info("Returning all EventAttendeeHist objects");
        return repo.findAll();
    }

    public List<EventAttendeeHist> findByUid(String uid)
    {
        logger.info("Returning all EventAttendeeHist by uId");
        return repo.findByUid(uid);
    }

    public List<EventAttendeeHist> findByPersonUid(String uid)
    {
        logger.info("Returning all EventAttendeeHist by personUID :{}" , uid);
        return repo.findByPersonUid(uid);
    }

    public List<EventAttendeeHist> findByEventUid(String uid)
    {
        logger.info("Returning all EventAttendeeHist by eventUId");
        return repo.findByEventUid(uid);
    }

    public List<EventAttendeeHist> findByEventUidAndPersonUid(String eventUid,String personUid)
    {
        logger.info("Returning all EventAttendeeHist by eventUId");
        return repo.findByEventUidAndPersonUid(eventUid,personUid);
    }

    public List<EventAttendeeHist> findByEventUidAndCheckedIn(String eventUid,String checkedIn) {
        logger.info("Returning all EventAttendeeHist by eventUId {} that are checkedIn {}", eventUid, checkedIn);
        List<EventAttendeeHist> eventAttendees = repo.findByEventUid(eventUid)
                .stream()
                .filter(e -> e.getCheckedIn() == checkedIn).toList();
        return eventAttendees;
    }

    public EventAttendeeHist createRecord(EventAttendeeHist eventAttendeeHist) throws EventAttendeeException {

        //name and venue are mandatory fields
        if (    eventAttendeeHist.getUid() == null || eventAttendeeHist.getUid().trim().equals("") ||
                eventAttendeeHist.getEventUid() == null || eventAttendeeHist.getEventUid().trim().equals("")||
                eventAttendeeHist.getPersonUid()== null || eventAttendeeHist.getPersonUid().trim().equals(""))
        {
            throw new EventAttendeeException("UID, eventUid & personUid must be entered to create data");
        }
        else
        {
            //No need to look it up - we are creating a record for each event
            logger.info("Creating new EventAttendeeHist record: {}",eventAttendeeHist);
            return repo.save(eventAttendeeHist);
        }
    }

    public void delete(EventAttendeeHist eventAttendeeHist) throws EventAttendeeException, EventAttendeeNotFoundException
    {
        if (    eventAttendeeHist.getUid() == null|| eventAttendeeHist.getUid().trim().equals("") )
        {
            throw new EventAttendeeException("UID must be provided. Unable to remove");
        }
        else
        {
            logger.info("Removing the EventAttendeeHist record: {}" , eventAttendeeHist);
            List<EventAttendeeHist> result = repo.findByUid(eventAttendeeHist.getUid());
            if (!result.isEmpty() && result.get(0).getUid().equals(eventAttendeeHist.getUid())) {
                repo.delete(eventAttendeeHist);
            }
            else
            {
                logger.info("EventAttendeeHist with UID: {} not found" , eventAttendeeHist.getUid());
                throw new EventAttendeeNotFoundException("EventAttendee with UID: " + eventAttendeeHist.getEventUid() + " not found");
            }
        }
    }

    public EventAttendeeHist update(EventAttendeeHist event) throws EventAttendeeException {
        if (event.getUid() == null|| event.getUid().trim().equals("")||
                event.getEventUid() == null|| event.getPersonUid().trim().equals(""))
        {
            throw new EventAttendeeException("Unable to update mandatory values are empty. EventUid, PersonUid");
        }
        else
        {
            logger.info("Updating the EventAttendeeHist record: {}" , event);
            return repo.save(event);
        }
    }
}
