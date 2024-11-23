package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.service.EventAttendeeHistService;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/eventattendeehist")

public class EventAttendeeHistController {

    private static final Logger logger = LoggerFactory.getLogger(EventAttendeeHistController.class);

    @Autowired
    EventAttendeeHistService service;

    private static final String TABLE_NAME = "EventAttendeeHist";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";



    /**
     * Find all EventAttendee Records
     *
     * @return all EventAttendee Records
     */
    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {

        Map<String, List<EventAttendeeHist>> response = new HashMap<>(1);
        try {
            List<EventAttendeeHist> eventAttendeeHistList = service.findAll();
            if (eventAttendeeHistList.isEmpty()) {
                return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, eventAttendeeHistList);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findbyevent")
    ResponseEntity<Object> findByEventUid(
            @RequestParam(value = "enventuid") String eventuid) {
        Map<String, List<EventAttendeeHist>> response = new HashMap<>(1);
        try {
            if (!eventuid.isEmpty()) {

                List<EventAttendeeHist> eventAttendeeHistList = service.findByEventUid(eventuid);
                if (eventAttendeeHistList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeHistList);
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

    @GetMapping("/findbyperson")
    ResponseEntity<Object> findByPersonUid(
            @RequestParam(value = "personuid") String personuid) {
        Map<String, List<EventAttendeeHist>> response = new HashMap<>(1);
        try {
            if (!personuid.isEmpty()) {

                List<EventAttendeeHist> eventAttendeeHistList = service.findByPersonUid(personuid);
                if (eventAttendeeHistList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeHistList);
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

    @GetMapping("/find")
    ResponseEntity<Object> findByEventUidAndCheckedIn(
            @RequestParam(value = "eventUid") String eventUid,
            @RequestParam(value = "checkedIn") String checkedIn) {
        Map<String, List<EventAttendeeHist>> response = new HashMap<>(1);
        try {
            if (!eventUid.isEmpty()) {

                List<EventAttendeeHist> eventAttendeeHistList = service.findByEventUidAndCheckedIn(eventUid,checkedIn);
                if (eventAttendeeHistList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeHistList);
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
    public ResponseEntity<Object> createEventAttendeeHist(@RequestBody EventAttendeeHist eventAttendeeHist) {
        try {

            EventAttendeeHist result;
            //Lookup the DB for an existing record
            List<EventAttendeeHist> lookUpEventAttendees = service.findByEventUidAndPersonUid(eventAttendeeHist.getEventUid(),eventAttendeeHist.getPersonUid());
            if (lookUpEventAttendees.size() == 0) {
                //Person not found create new record
                eventAttendeeHist.setUid(EventUtils.generateType1UUID().toString());
                eventAttendeeHist.setSortKey(eventAttendeeHist.getEventUid() + eventAttendeeHist.getPersonUid());
                logger.info("Creating new record: {}", eventAttendeeHist);

                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                String now = sdf.format(new Date());
                eventAttendeeHist.setHistDate(now);
                result = service.createRecord(eventAttendeeHist);
            } else {
                result = lookUpEventAttendees.get(0);
            }

            Map<String, EventAttendeeHist> response = new HashMap<>(1);
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
    public EventAttendeeHist update(@RequestBody EventAttendeeHist eventAttendeeHist) {
        try {

            if (eventAttendeeHist != null && !eventAttendeeHist.getUid().isEmpty()) {
                //Find if the record exists
                List<EventAttendeeHist> currentEvent = service.findByEventUidAndPersonUid(eventAttendeeHist.getUid(),eventAttendeeHist.getPersonUid());
                if (!currentEvent.isEmpty()) {
                    return service.update(eventAttendeeHist);
                } else {

                    logger.info("EventAttendee not found in the db: {}. No updates", eventAttendeeHist);
                    throw new EventAttendeeException("EventAttendee not found in the db: " + eventAttendeeHist + ". No updates");
                }
            }
        } catch (EventAttendeeException ex) {
            logger.error(ex.getMessage());
        }
        finally {
            return eventAttendeeHist;
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Object> remove(@PathVariable("uid") String uid) throws EventAttendeeNotFoundException {

        Map<String, List<String>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<EventAttendeeHist> eventAttendeeHistList = service.findByUid(uid);
                if (eventAttendeeHistList.size() >0 && eventAttendeeHistList.get(0) == null) {
                    throw new EventAttendeeNotFoundException(uid);
                }

                service.delete(eventAttendeeHistList.get(0));


                List<String> details = new ArrayList<>();
                details.add("EventAttendHist with UID: " + uid + " removed");

                response.put(TABLE_NAME, details);

                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (EventAttendeeNotFoundException pe) {
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

    @GetMapping("/checkin")
    public ResponseEntity<Object> checkin(
            @RequestParam(value = "eventUID") String eventUid,
            @RequestParam(value = "personUID") String personUid,
            @RequestParam(value = "checkIn") Boolean checkin) throws EventAttendeeNotFoundException
    {

        EventAttendeeHist eventAttendeeHist = new EventAttendeeHist();
        try {
            //Check for input values
            if ((eventUid.isEmpty() || eventUid.equals("")) &&
                    (personUid.isEmpty() || personUid.equals(""))) {
                throw new EventAttendeeNotFoundException("Event UID: " + eventUid + " personUid: " + personUid);
            }

            //Find the EventAttendee Record
            List<EventAttendeeHist> eventAttendeeHists = service.findByEventUidAndPersonUid(eventUid, personUid);
            if (eventAttendeeHists.size() > 0) {
                //Update the record with the value for Checkin
                eventAttendeeHist = eventAttendeeHists.get(0);
                if (checkin) {
                    eventAttendeeHist.setCheckedIn("true");
                } else {
                    eventAttendeeHist.setCheckedIn("false");
                }

                eventAttendeeHist = service.update(eventAttendeeHist);

                Map<String, EventAttendeeHist> response = new HashMap<>(1);
                response.put(TABLE_NAME, eventAttendeeHist);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        catch (EventAttendeeException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    finally {
            Map<String, EventAttendeeHist> response = new HashMap<>(1);
            response.put(TABLE_NAME, eventAttendeeHist);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

}
