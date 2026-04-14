package com.sundram.expense_tracker.data.repository;

import com.sundram.expense_tracker.data.local.dao.ExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<ExpenseDao> daoProvider;

  public ExpenseRepositoryImpl_Factory(Provider<ExpenseDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(Provider<ExpenseDao> daoProvider) {
    return new ExpenseRepositoryImpl_Factory(daoProvider);
  }

  public static ExpenseRepositoryImpl newInstance(ExpenseDao dao) {
    return new ExpenseRepositoryImpl(dao);
  }
}
