//package jpabasic.jpashop;
//
//import jpabasic.ex1hellojpa.Team;
//import jpabasic.jpashop.domain.Book;
//import jpabasic.jpashop.domain.Order;
//import jpabasic.jpashop.domain.OrderItem;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import javax.persistence.Persistence;
//
//public class JpaMain {
//    public static void main(String[] args) {
//
//        EntityManagerFactory emf =  Persistence.createEntityManagerFactory("hello");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx =  em.getTransaction();
//        tx.begin();
//
//        try {
//
//            Book book = new Book();
//            book.setName("JPA");
//            book.setAuthor("κΉμν");
//
//            em.persist(book);
//
//        } catch(Exception e) {
//            tx.rollback();
//        } finally {
//            em.close();
//
//        }
//        emf.close();
//    }
//}
