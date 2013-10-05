package marxo.controller;

import marxo.bean.Tenant;
import marxo.dao.TenantDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("tenant{:s?}")
public class TenantController extends BasicController<Tenant, TenantDao> {
    @Autowired
    TenantDao tenantDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Tenant> getAll() {
        return tenantDao.findAll();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Tenant create(@Valid @RequestBody Tenant entity) throws Exception {
        if (tenantDao.exists(entity.getId())) {
            throw new EntityExistsException(entity.getId());
        }

        try {
            tenantDao.save(entity);
        } catch (ValidationException ex) {
            // todo: add error message
            throw new EntityInvalidException(entity.getId(), "not implemented");
        }

        return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Tenant read(@PathVariable String id) {
        if (!ObjectId.isValid(id)) {
            throw new InvalidObjectIdException(id);
        }

        ObjectId objectId = new ObjectId(id);
        Tenant tenant = tenantDao.get(objectId);

        if (tenant == null) {
            throw new EntityNotFoundException(objectId);
        }

        return tenant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    // fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
    // review: is the parameter 'id' necessary?
    public Tenant update(@Valid @PathVariable String id, @Valid @RequestBody Tenant Tenant) {
        if (!ObjectId.isValid(id)) {
            throw new InvalidObjectIdException(id);
        }

        ObjectId objectId = new ObjectId(id);

        // todo: check consistency of given id and entity id.
        Tenant oldTenant = tenantDao.get(objectId);

        if (oldTenant == null) {
            throw new EntityNotFoundException(objectId);
        }

        try {
            tenantDao.save(oldTenant);
        } catch (ValidationException e) {
//			e.reasons.toString()
//			throw new EntityInvalidException(objectId, );
        }

        return oldTenant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    // fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
    public Tenant delete(@PathVariable String id) {
        if (!ObjectId.isValid(id)) {
            throw new InvalidObjectIdException(id);
        }

        ObjectId objectId = new ObjectId(id);
        Tenant tenant = tenantDao.deleteById(objectId);

        if (tenant == null) {
            throw new EntityNotFoundException(objectId);
        }

        return tenant;
    }
}
