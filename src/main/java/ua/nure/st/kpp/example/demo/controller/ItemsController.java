package ua.nure.st.kpp.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.form.item.AddItemForm;
import ua.nure.st.kpp.example.demo.form.item.EditItemForm;
import ua.nure.st.kpp.example.demo.memento.EditItemFormState;
import ua.nure.st.kpp.example.demo.memento.EditItemMemento;
import ua.nure.st.kpp.example.demo.memento.MementoMetadata;
import ua.nure.st.kpp.example.demo.memento.MementoResponse;
import ua.nure.st.kpp.example.demo.service.TransformerService;

import static ua.nure.st.kpp.example.demo.util.SessionAttributes.EDIT_ITEM_MEMENTO;


@Controller
@RequestMapping("/items")
@SessionAttributes(EDIT_ITEM_MEMENTO)
public class ItemsController {
    private final ItemDAO itemDAO;
    private final TransformerService transformerService;

    @Autowired
    public ItemsController(ItemDAO itemDAO, TransformerService transformerService) {
        this.transformerService = transformerService;
        this.itemDAO = itemDAO;
    }

    @GetMapping()
    public String showAllItems(Model model) throws DAOException {
        model.addAttribute("itemsList", itemDAO.readAll());
        return "items/items";
    }

    @GetMapping("/search")
    public String showAllItemsByName(@RequestParam("name") String itemName,
                                     Model model) throws DAOException {
        model.addAttribute("itemsList", itemDAO.readAllByName(itemName));
        return "items/items";
    }

    @GetMapping("/add")
    public String newItem(@ModelAttribute("addItemForm") AddItemForm addItemForm) {
        return "items/addItem";
    }

    @PostMapping()
    public String createItem(@ModelAttribute("addItemForm") @Validated AddItemForm addItemForm,
                             BindingResult bindingResult) throws DAOException {
        if (bindingResult.hasErrors()) {
            return "items/addItem";
        }
        Item item = transformerService.toItem(addItemForm);
        item.setAmount(0);
        itemDAO.create(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String editItem(Model model, @PathVariable("id") int id,
                           @ModelAttribute(EDIT_ITEM_MEMENTO) EditItemMemento editItemMemento) throws DAOException {
        EditItemForm editItemForm = transformerService.toEditItemForm(itemDAO.read(id));
        if (editItemMemento.getStateSize() != 0 &&
                !editItemMemento.current().getId().equals(String.valueOf(editItemForm.getId()))) {
            editItemMemento.clear();
        }
        if (editItemMemento.getStateSize() == 0) {
            model.addAttribute("editItemForm", editItemForm);
            editItemMemento.saveState(transformerService.toState(editItemForm));
        }else {
            EditItemForm currentStateForm = transformerService.toForm(editItemMemento.current());
            model.addAttribute("editItemForm",currentStateForm);
        }
        return"items/editItem";
}

    @ModelAttribute(EDIT_ITEM_MEMENTO)
    public EditItemMemento editItemMemento() {
        return new EditItemMemento();
    }

    @PostMapping(value = "/edit/state/save", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MementoMetadata saveState(@ModelAttribute(EDIT_ITEM_MEMENTO) EditItemMemento editItemMemento,
                                     @RequestBody EditItemFormState editItemFormState) {
        editItemMemento.saveState(editItemFormState);

        return editItemMemento.getMetadata();
    }

    @GetMapping("/edit/state")
    @ResponseBody
    public MementoMetadata getMementoMetadata(@ModelAttribute(EDIT_ITEM_MEMENTO) EditItemMemento editItemMemento) {
        return editItemMemento.getMetadata();
    }

    @GetMapping("/edit/state/previous")
    @ResponseBody
    public MementoResponse<EditItemFormState> getPreviousEditMementoState(@ModelAttribute(EDIT_ITEM_MEMENTO) EditItemMemento editItemMemento) {
        EditItemFormState previousState = editItemMemento.previous();
        MementoMetadata metadata = editItemMemento.getMetadata();

        return new MementoResponse<>(metadata,previousState);
    }

    @GetMapping("/edit/state/next")
    @ResponseBody
    public MementoResponse<EditItemFormState> getNextEditMementoState(@ModelAttribute(EDIT_ITEM_MEMENTO) EditItemMemento editItemMemento) {
        EditItemFormState nextState = editItemMemento.next();
        MementoMetadata metadata = editItemMemento.getMetadata();

        return new MementoResponse<>(metadata,nextState);
    }


    @PatchMapping("/{id}")
    public String updateItem(@ModelAttribute("editItemForm") @Validated EditItemForm editItemForm, BindingResult bindingResult, @PathVariable("id") int id) throws DAOException {
        if (bindingResult.hasErrors()) {
            return "items/editItem";
        }
        Item item = transformerService.toItem(editItemForm);
        itemDAO.update(id, item);
        return "redirect:/items";
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable("id") int id) throws DAOException {
        itemDAO.delete(id);
        return "redirect:/items";
    }
}
