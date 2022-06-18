package jpabasic.ex1hellojpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

@SpringBootApplication
public class Ex1HelloJpaApplication {

    public static void main(String[] args) {

//        SpringApplication.run(Ex1HelloJpaApplication.class, args);
        EntityManagerFactory emf =  Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx =  em.getTransaction();
        tx.begin();

        try {
//            Member findMember = em.find(Member.class, 1L);
            List<Member> findMembers = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList();

            for (Member findMember : findMembers) {
                System.out.println("findMember.id  :" + findMember.getId());
                System.out.println("findMember.name  :" + findMember.getName());
            }

//            findMember.setName("HelloJPA");
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close();

        }
        emf.close();
    }

}
