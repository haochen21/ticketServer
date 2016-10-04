package ticket.server.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class ExtendedJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements BaseRepository<T, ID> {

	private final EntityManager entityManager;

	public ExtendedJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public EntityManager getEm() {
		return this.entityManager;
	}

	@Override
	public <S extends T> S merge(S entity) {
		return this.entityManager.merge(entity);
	}

	@Override
	public <S extends T> S getReference(Class<S> domainClass, ID id) {
		return this.entityManager.getReference(domainClass, id);
	}
}