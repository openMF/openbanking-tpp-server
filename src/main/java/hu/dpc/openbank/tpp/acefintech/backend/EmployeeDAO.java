package hu.dpc.openbank.tpp.acefintech.backend;

import org.springframework.stereotype.Component;

@Component
public class EmployeeDAO implements Dao<Employees> {
    public Employees get() {
        Employees e = new Employees("Valaki","dev@null");
        return e;
    }
}
