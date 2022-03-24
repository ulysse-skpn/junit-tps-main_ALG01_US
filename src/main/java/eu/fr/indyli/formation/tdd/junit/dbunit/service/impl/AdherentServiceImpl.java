package eu.fr.indyli.formation.tdd.junit.dbunit.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import eu.fr.indyli.formation.tdd.junit.dbunit.dao.IAdherentRepository;
import eu.fr.indyli.formation.tdd.junit.dbunit.entity.Adherent;
import eu.fr.indyli.formation.tdd.junit.dbunit.service.IAdherentService;

@Service("adherentService")
public class AdherentServiceImpl implements IAdherentService {

  @Resource(name = "adherentRepository")
  private IAdherentRepository adherentRepository = null;

  @Override
  public Adherent findById(Long id) {
    return adherentRepository.findById(id).orElse(null);
  }

  @Override
  public Adherent save(Adherent a) {
    return adherentRepository.save(a);
  }

  @Override
  public Adherent findByLastname(String lastname) {
    return adherentRepository.findByLastname(lastname).orElse(null);
  }

  @Override
  public List<Adherent> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void update(Adherent a) {
    adherentRepository.saveAndFlush(a);
  }

}
