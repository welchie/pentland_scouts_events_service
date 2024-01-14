package uk.org.pentlandscouts.events.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.repositories.EventAttendeeRepository;


import java.util.List;

/**
 * Service layer for Crud event for Person entity
 */
@Service
public class EventAttendeeService {

    private static final Logger logger = LoggerFactory.getLogger(EventAttendeeService.class);

    @Autowired
    EventAttendeeRepository repo;

    public List<EventAttendee> findAll()
    {
        logger.info("Returning all EventAttendees objects");
        return repo.findAll();
    }

    public List<EventAttendee> findByUid(String uid)
    {
        logger.info("Returning all EventAttendees by uId");
        return repo.findByUid(uid);
    }

    public List<EventAttendee> findByPersonUid(String uid)
    {
        logger.info("Returning all EventAttendees by personUID");
        return repo.findByPersonUid(uid);
    }

    public List<EventAttendee> findByEventUid(String uid)
    {
        logger.info("Returning all EventAttendees by eventUId");
        return repo.findByEventUid(uid);
    }

    public List<EventAttendee> findByEventUidAndPersonUid(String eventUid,String personUid)
    {
        logger.info("Returning all EventAttendees by eventUId");
        return repo.findByEventUidAndPersonUid(eventUid,personUid);
    }

    public EventAttendee createRecord(EventAttendee eventAttendee) throws EventAttendeeException {

        //name and venue are mandatory fields
        if (eventAttendee.getEventUid() == null || eventAttendee.getEventUid().trim().equals("")||
                eventAttendee.getPersonUid()== null || eventAttendee.getPersonUid().trim().equals(""))
        {
            throw new EventAttendeeException("eventUid & personUid must be entered to create data");
        }
        else
        {
            logger.info("Creating new EventAttendees record: {}",eventAttendee);
            return repo.save(eventAttendee);
        }
    }

    public void delete(EventAttendee eventAttendee) throws EventAttendeeException, EventAttendeeNotFoundException
    {
        if (    eventAttendee.getEventUid() == null|| eventAttendee.getEventUid().trim().equals("") &&
                eventAttendee.getPersonUid() == null|| eventAttendee.getPersonUid().trim().equals(""))
        {
            throw new EventAttendeeException("EventUID & PersonUid must be provided. Unable to remove");
        }
        else
        {
            logger.info("Removing the EventAttendees record: {}" , eventAttendee);
            List<EventAttendee> result = repo.findByEventUid(eventAttendee.getEventUid());
            if (!result.isEmpty() && result.get(0).getUid().equals(eventAttendee.getUid())) {
                repo.delete(eventAttendee);
            }
            else
            {
                logger.info("EventAttendee with EventUID: {} & PersonIOD: {} not found" , eventAttendee.getUid(), eventAttendee.getPersonUid());
                throw new EventAttendeeNotFoundException("EventAttendee with UID: " + eventAttendee.getEventUid() + " not found");
            }
        }
    }

    public EventAttendee update(EventAttendee event) throws EventAttendeeException {
        if (event.getUid() == null|| event.getUid().trim().equals("")||
                event.getEventUid() == null|| event.getPersonUid().trim().equals(""))
        {
            throw new EventAttendeeException("Unable to update mandatory values are empty. EventUid, PersonUid");
        }
        else
        {
            logger.info("Updating the EventAttendee record: {}" , event);
            return repo.save(event);
        }
    }
}
