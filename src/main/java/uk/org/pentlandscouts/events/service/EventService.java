package uk.org.pentlandscouts.events.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.pentlandscouts.events.exception.EventException;
import uk.org.pentlandscouts.events.exception.EventNotFoundException;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.EventId;
import uk.org.pentlandscouts.events.repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Crud event for Person entity
 */
@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    EventRepository repo;

    public List<Event> findAll()
    {
        logger.info("Returning all Person objects");
        return repo.findAll();
    }

    public List<Event> findByUid(String uid)
    {
        logger.info("Finding Event by id:{}",uid);

        List<Event> results = repo.findByUid(uid);

        return results;
    }

    public List<Event> findByNameAndVenue(String name, String venue)
    {
        logger.info("Find Event by:{} and {}",name,venue);
        return repo.findByNameAndVenue(name,venue);
    }

    public Event createRecord(Event event) throws EventException {

        //name and venue are mandatory fields
        if (event.getName() == null || event.getName().trim().equals("")||
                event.getVenue()== null || event.getVenue().trim().equals(""))
        {
            throw new EventException("Event name & venue must be entered to create data");
        }
        else
        {
            logger.info("Creating new Event record: {}",event);
            return repo.save(event);
        }
    }

    public Event update(Event event) throws EventException {
        if (event.getUid() == null|| event.getUid().trim().equals("")||
                event.getName() == null|| event.getName().trim().equals("")||
                event.getVenue()== null || event.getVenue().trim().equals(""))
        {
            throw new EventException("Unable to update mandatory values are empty. UID, name, venue");
        }
        else
        {
            logger.info("Updating the Event record: {}" , event);
            return repo.save(event);
        }
    }

    public void delete(Event event) throws EventException, EventNotFoundException
    {
        if (event.getUid() == null|| event.getUid().trim().equals(""))
        {
            throw new EventException("Event UID must be provided. Unable to remove");
        }
        else
        {
            logger.info("Removing the Event record: {}" , event);
            List<Event> result = repo.findByUid(event.getUid());
            if (!result.isEmpty() && result.get(0).getUid().equals(event.getUid())) {
                repo.delete(event);
            }
            else
            {
                logger.info("Event with UID: {} not found" , event.getUid());
                throw new EventNotFoundException("Event with UID: " + event.getUid() + " not found");
            }
        }
    }

}
