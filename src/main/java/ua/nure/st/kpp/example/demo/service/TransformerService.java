package ua.nure.st.kpp.example.demo.service;

import org.springframework.stereotype.Service;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Record;
import ua.nure.st.kpp.example.demo.entity.Role;
import ua.nure.st.kpp.example.demo.entity.User;
import ua.nure.st.kpp.example.demo.form.company.AddCompanyForm;
import ua.nure.st.kpp.example.demo.form.company.EditCompanyForm;
import ua.nure.st.kpp.example.demo.form.item.AddItemForm;
import ua.nure.st.kpp.example.demo.form.item.EditItemForm;
import ua.nure.st.kpp.example.demo.form.journal.AddRecordForm;
import ua.nure.st.kpp.example.demo.form.journal.EditRecordForm;
import ua.nure.st.kpp.example.demo.form.user.RegistrationForm;
import ua.nure.st.kpp.example.demo.memento.EditItemFormState;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransformerService {
    public EditItemForm toEditItemForm(Item item) {
        EditItemForm itemForm = new EditItemForm();
        itemForm.setName(item.getName());
        itemForm.setVendor(item.getVendor());
        itemForm.setUnit(item.getUnit());
        itemForm.setWeight(item.getWeight());
        itemForm.setReserveRate(item.getReserveRate());
        itemForm.setId(item.getId());
        return itemForm;
    }

    public Item toItem(AddItemForm addItemForm) {
        return new Item.Builder<>()
                .vendor(addItemForm.getVendor())
                .name(addItemForm.getName())
                .unit(addItemForm.getUnit())
                .weight(addItemForm.getWeight())
                .reserveRate(addItemForm.getReserveRate())
                .build();
    }

    public Item toItem(EditItemForm editItemForm) {
        return new Item.Builder<>()
                .vendor(editItemForm.getVendor())
                .name(editItemForm.getName())
                .unit(editItemForm.getUnit())
                .weight(editItemForm.getWeight())
                .reserveRate(editItemForm.getReserveRate())
                .build();
    }

    public Company toCompany(AddCompanyForm addCompanyForm) {
        return new Company.Builder<>()
                .name(addCompanyForm.getName())
                .email(addCompanyForm.getEmail())
                .address(addCompanyForm.getAddress())
                .build();
    }

    public EditCompanyForm toEditCompanyForm(Company company) {
        EditCompanyForm editCompanyForm = new EditCompanyForm();
        editCompanyForm.setId(company.getId());
        editCompanyForm.setName(company.getName());
        editCompanyForm.setEmail(company.getEmail());
        editCompanyForm.setAddress(company.getAddress());
        return editCompanyForm;
    }

    public Company toCompany(EditCompanyForm editCompanyForm) {
        return new Company.Builder<>()
                .name(editCompanyForm.getName())
                .email(editCompanyForm.getEmail())
                .address(editCompanyForm.getAddress())
                .id(editCompanyForm.getId())
                .build();
    }

    public EditRecordForm toEditRecordForm(Record record) {
        EditRecordForm editRecordForm = new EditRecordForm();
        editRecordForm.setDocumentNumber(record.getDocumentNumber());
        editRecordForm.setId(record.getId());
        editRecordForm.setItemId(record.getItem().getId());
        editRecordForm.setCompanyId(record.getCompany().getId());
        editRecordForm.setPrice(record.getPrice());
        editRecordForm.setAmount(record.getAmount());
        return editRecordForm;
    }

    public Record toRecord(AddRecordForm addRecordForm) {
        Item item = new Item.Builder<>().id(addRecordForm.getItemId()).build();
        Company company = new Company.Builder<>().id(addRecordForm.getCompanyId()).build();
        return new Record.Builder<>()
                .amount(addRecordForm.getAmount())
                .price(addRecordForm.getPrice())
                .documentNumber(addRecordForm.getDocumentNumber())
                .amount(addRecordForm.getAmount())
                .item(item)
                .company(company)
                .build();
    }

    public Record toRecord(EditRecordForm editRecordForm) {
        Item item = new Item.Builder<>().id(editRecordForm.getItemId()).build();
        Company company = new Company.Builder<>().id(editRecordForm.getCompanyId()).build();
        return new Record.Builder<>()
                .id(editRecordForm.getId())
                .amount(editRecordForm.getAmount())
                .price(editRecordForm.getPrice())
                .documentNumber(editRecordForm.getDocumentNumber())
                .amount(editRecordForm.getAmount())
                .item(item)
                .company(company)
                .build();
    }

    public EditItemFormState toState(EditItemForm editItemForm){
        return new EditItemFormState(
                String.valueOf(editItemForm.getId()),
                editItemForm.getVendor(),
                editItemForm.getName(),
                editItemForm.getUnit(),
                editItemForm.getWeight().toString(),
                String.valueOf(editItemForm.getReserveRate())
        );
    }

    public EditItemForm toForm(EditItemFormState state) {
        return new EditItemForm(
                Integer.parseInt(state.getId()),
                state.getVendor(),
                state.getName(),
                state.getUnit(),
                BigDecimal.valueOf(Double.valueOf(state.getWeight())).setScale(4, RoundingMode.FLOOR),
                Integer.parseInt(state.getReserveRate())
        );
    }

    public User toUser(RegistrationForm registrationForm) {
        return  new User(
                registrationForm.getUsername(),
                registrationForm.getPassword(),
                Role.getInstance(registrationForm.getRole())
        );
    }
}
