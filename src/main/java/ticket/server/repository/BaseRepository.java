package ticket.server.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	EntityManager getEm();

	<S extends T> S merge(S entity);

	<S extends T> S getReference(Class<S> domainClass, ID id);
}
