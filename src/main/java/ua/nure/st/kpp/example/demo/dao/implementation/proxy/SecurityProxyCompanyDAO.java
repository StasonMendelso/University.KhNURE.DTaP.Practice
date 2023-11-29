package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class SecurityProxyCompanyDAO extends SecurityProxy implements CompanyDAO {

    private final CompanyDAO companyDAO;

    public SecurityProxyCompanyDAO(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }


    @Override
    public boolean create(Company company) throws DAOException {
        checkAccessForModification(getUserDetails());
        return companyDAO.create(company);
    }

    @Override
    public List<Company> readAll() throws DAOException {
        return companyDAO.readAll();
    }

    @Override
    public boolean update(int id, Company company) throws DAOException {
        checkAccessForModification(getUserDetails());
        return companyDAO.update(id,company);
    }

    @Override
    public Company read(int id) throws DAOException {
        return companyDAO.read(id);
    }

    @Override
    public List<Integer> readAllAvailableId() throws DAOException {
        return companyDAO.readAllAvailableId();
    }
}
