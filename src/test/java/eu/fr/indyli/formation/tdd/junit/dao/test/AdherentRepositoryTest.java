package eu.fr.indyli.formation.tdd.junit.dao.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import eu.fr.indyli.formation.tdd.junit.dbunit.app.Application;
import eu.fr.indyli.formation.tdd.junit.dbunit.dao.IAdherentRepository;
import eu.fr.indyli.formation.tdd.junit.dbunit.entity.Adherent;

/** it is preferable to use an abstract class. Here for example only. */
@DatabaseSetup("/adherent.xml")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestExecutionListeners({
    TransactionalTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
public class AdherentRepositoryTest {

  @Resource(name = "adherentRepository")
  private IAdherentRepository adherentRepository;

  @Test
  public void findByName_WhenExist_ShouldBeExist() {
    // given
    Adherent expected = new Adherent("PHILIPPE", "EDOUARD", 45);
    expected.setId(1L);

    // when
    Optional<Adherent> actual = adherentRepository.findByLastname("PHILIPPE");

    // then
    assertThat(actual, equalTo(Optional.of(expected)));
  }

  @Test
  public void findByName_WhenNotExist_ShouldBeNull() {
    // when
    Optional<Adherent> actual = adherentRepository.findByLastname("NotExist");

    // then
    assertThat(actual, equalTo(Optional.empty()));
  }

  @Test
  public void createNew_ShouldBeMore() {
    // given
    Adherent employee = new Adherent("John", "admin", 72);
    Integer listSizeBefore = adherentRepository.findAll().size();

    // when
    adherentRepository.save(employee);

    // then
    List<Adherent> all = adherentRepository.findAll();
    assertThat(all.size(), equalTo(listSizeBefore + 1));
  }


  @Test
  public void update_age_ShouldBeEffective() {
    // given
    Long id = 2L;
    Adherent ad = this.adherentRepository.findById(id).orElse(null);
    Integer ageBefore = ad.getAge();

    // when
    Integer ageAfter = 45;
    ad.setAge(ageAfter);
    this.adherentRepository.saveAndFlush(ad);

    // then
    ad = this.adherentRepository.findById(id).orElse(null);
    Integer retrieveAgeAfter = ad.getAge();
    assertTrue(ageAfter.equals(retrieveAgeAfter) && !ageAfter.equals(ageBefore));
  }


  @Test
  public void findByAgeGreater_WhenExist_ShouldBeExist() {
    // given
    Integer age = 50;
    Integer expectedNbAdherent = 2;

    // when
    List<Adherent> adherents = adherentRepository.findByAgeGreaterThan(age);

    // then
    assertThat(expectedNbAdherent, equalTo(adherents.size()));
  }

  @Test
  public void delete_By_Id_WhenExist_ShouldBeDelete() {
    // given
    Long idAdherent = 2L;
    Integer sizeBefore = adherentRepository.findAll().size();

    // when
    this.adherentRepository.deleteById(idAdherent);
    Integer sizeAfter = adherentRepository.findAll().size();

    // then
    assertThat(sizeBefore, equalTo(sizeAfter + 1));
  }

}
