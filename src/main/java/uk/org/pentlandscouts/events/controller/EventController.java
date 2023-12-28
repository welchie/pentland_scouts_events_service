package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.EventException;
import uk.org.pentlandscouts.events.exception.EventNotFoundException;
import uk.org.pentlandscouts.events.exception.PersonNotFoundException;
import uk.org.pentlandscouts.events.model.Event;
import uk.org.pentlandscouts.events.model.domain.EventEmergencyContact;
import uk.org.pentlandscouts.events.service.EventService;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")

public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    EventService service;

    private static final String TABLE_NAME = "Event";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";


    @GetMapping("/emergencycontact/{uid}")
    public ResponseEntity<Object> getPerson(@PathVariable("uid") String uid) throws PersonNotFoundException {

        Map<String, List<EventEmergencyContact>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<Event> eventList = service.findByUid(uid);
                if (eventList.size() >0 && eventList.get(0) == null) {
                    throw new PersonNotFoundException(uid);
                }

                //Convert Person to JSON
                Event event = eventList.get(0);
                EventEmergencyContact eventEmergencyContact = new EventEmergencyContact();
                eventEmergencyContact.setUid(event.getUid());
                eventEmergencyContact.setName(event.getName());
                eventEmergencyContact.setVenue(event.getVenue());
                eventEmergencyContact.setContactName(event.getEmergencyContactName());
                eventEmergencyContact.setContactNo(event.getEmergencyContactNo());
                eventEmergencyContact.setStartDate(event.getStartDate());
                eventEmergencyContact.setEndDate(event.getEndDate());

                List<EventEmergencyContact> details = new ArrayList<>();
                details.add(eventEmergencyContact);

                response.put(TABLE_NAME, details);

                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (PersonNotFoundException pe) {
            return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Find all Events
     *
     * @return
     */
    @GetMapping("/all")
    public List<Event> findAll() {
        return service.findAll();
    }

    @GetMapping("/find")
    ResponseEntity<Object> findByFirstNameLastNameDob(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "venue") String venue) {
        Map<String, List<Event>> response = new HashMap<>(1);
        try {
            if (!name.isEmpty() && !venue.isEmpty() ) {

                List<Event> eventList = service.findByNameAndVenue(name,venue);
                if (eventList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/find/{uid}")
    ResponseEntity<Object> findByUid(
            @PathVariable("uid") String uid) {
        Map<String, List<Event>> response = new HashMap<>(1);
        try {
            if (!uid.isEmpty()) {

                List<Event> eventList = service.findByUid(uid);
                if (eventList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createEvent(@RequestBody Event event) {
        try {

            Event result = null;
            //Lookup the DB for an existing record
            List<Event> lookUpEvents = service.findByNameAndVenue(event.getName(),event.getVenue());
            if (lookUpEvents.size() == 0) {
                //Person not found create new record
                event.setUid(EventUtils.generateType1UUID().toString());
                event.setSortKey(event.getName() + event.getVenue());
                logger.info("Creating new record: {}", event);


                result = service.createRecord(event);
            } else {
                result = lookUpEvents.get(0);
            }

            Map<String, Event> response = new HashMap<>(1);
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public Event update(@RequestBody Event updEvent) {
        try {

            if (updEvent != null && !updEvent.getUid().isEmpty()) {
                //Find if the record exists
                List<Event> currentEvent = service.findByUid(updEvent.getUid());
                if (!currentEvent.isEmpty()) {
                    return service.update(updEvent);
                } else {

                    logger.info("Event not found in the db: {}. No updates", updEvent);
                    throw new EventException("Event not found in the db: " + updEvent + ". No updates");
                }
            }
        } catch (EventException ex) {
            logger.error(ex.getMessage());
        }
        finally {
            return updEvent;
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Object> removePerson(@PathVariable("uid") String uid) throws EventNotFoundException {

        Map<String, List<String>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<Event> eventList = service.findByUid(uid);
                if (eventList.size() >0 && eventList.get(0) == null) {
                    throw new EventNotFoundException(uid);
                }

                service.delete(eventList.get(0));


                List<String> details = new ArrayList<>();
                details.add("Event with UID: " + uid + " removed");

                response.put(TABLE_NAME, details);

                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (EventNotFoundException pe) {
            return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
