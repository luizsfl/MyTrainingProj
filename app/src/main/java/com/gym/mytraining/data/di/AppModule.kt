package com.programacustofrete.custofrete.data.di

import com.gym.mytraining.data.dataSource.TraningDataSource
import com.gym.mytraining.data.dataSource.TraningDataSourceImp
import com.gym.mytraining.data.dataSource.UsuarioDataSource
import com.gym.mytraining.data.dataSource.UsuarioDataSourceImp
import com.gym.mytraining.data.repository.TrainingRepository
import com.gym.mytraining.data.repository.TrainingRepositoryImp
import com.gym.mytraining.data.repository.UsuarioRepository
import com.gym.mytraining.data.repository.UsuarioRepositoryImp
import com.gym.mytraining.domain.useCase.training.TrainingGetAllUseCase
import com.gym.mytraining.domain.useCase.training.TrainingInsertUseCase
import com.gym.mytraining.domain.useCase.training.TrainingInteractor
import com.gym.mytraining.domain.useCase.training.TrainingInteractorImp
import com.gym.mytraining.domain.useCase.usuario.UsuarioInsertUseCase
import com.gym.mytraining.domain.useCase.usuario.UsuarioInteractor
import com.gym.mytraining.domain.useCase.usuario.UsuarioInteractorImp
import com.gym.mytraining.domain.useCase.usuario.UsuarioLogadoUseCase
import com.gym.mytraining.presentation.login.LoginViewModel
import com.gym.mytraining.presentation.newLogin.NewLoginViewModel
import com.gym.mytraining.presentation.newTraning.NewTrainingViewModel
import com.gym.mytraining.presentation.traning.TrainingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataSourceModule = module {
    factory<UsuarioDataSource> { UsuarioDataSourceImp() }
    factory<TraningDataSource> { TraningDataSourceImp() }
//    factory<EntregaRotaDataSource> { EntregaRotaDataSourceImp(entregaRotaDao = get()) }
//    factory<EntregaSimplesDataSource> { EntregaSimplesDataSourceImp(entregaSimplesDao = get()) }
}

val repositoryModule = module {
    factory<UsuarioRepository> { UsuarioRepositoryImp(usuarioDataSource = get()) }
    factory<TrainingRepository> { TrainingRepositoryImp( trainingDataSource = get()) }
//    factory<EntregaRotaRepository> { EntregaRotaRepositoryImp(entregaRotaDataSource = get()) }
//    factory<EntregaSimplesRepository> { EntregaSimplesRepositoryImp(entregaSimplesDataSource = get()) }
}

val useCaseModule = module {
    factory { UsuarioInsertUseCase(usuarioRepository = get()) }
    factory { UsuarioLogadoUseCase(usuarioRepository = get()) }

    factory { TrainingInsertUseCase( trainingRepository= get()) }
    factory { TrainingGetAllUseCase( trainingRepository= get()) }
//    factory { DadosVeiculoGetUseCase(dadosVeiculoRepository = get()) }
//    factory { EntregaRotaAddUseCase(entregaRotaRepository = get()) }
//    factory { EntregaRotaGetAllUseCase(entregaRotaRepository = get()) }
//    factory { EntregaRotaDeleteUseCase(entregaRotaRepository = get()) }
//    factory { EntregaRotaUpdateUseCase(entregaRotaRepository = get()) }
//    factory { EntregaSimplesAddUseCase(entregaSimplesRepository = get()) }
//    factory { EntregaSimplesGetAllUseCase(entregaSimplesRepository = get()) }
//    factory { EntregaSimplesDeleteUseCase(entregaSimplesRepository = get()) }
//    factory { EntregaSimplesUpdateUseCase(entregaSimplesRepository = get()) }
//    factory { EntregaRotaUpdateStatusUseCase(entregaRotaRepository = get()) }
}

val interactorModule = module {
    factory<UsuarioInteractor> {
        UsuarioInteractorImp(
            usuarioInsertUseCase = get(),
            usuarioVerificarUsuarioLogadoUseCase=get(),
        ) }

    factory<TrainingInteractor> {
        TrainingInteractorImp(
            trainingInsertUseCase = get(),
            trainingGetAllUseCase = get(),
        ) }


//    factory<EntregaRotaInteractor> {EntregaRotaInteractorImp(
//        entregaRotaAddUseCase = get(),
//        entregaRotaGetAllUseCase = get(),
//        entregaRotaDeleteUseCase = get(),
//        entregaRotaUpdateUseCase = get(),
//        entregaRotaUpdateStatusUseCase = get()
//    ) }


}

val viewModel = module {

    viewModel { NewLoginViewModel(usuarioInteractor = get()) }
    viewModel { LoginViewModel( usuarioInteractor = get()) }

    viewModel { TrainingViewModel( trainingInteractor = get())  }
    viewModel { NewTrainingViewModel( trainingInteractor = get())  }

//    viewModel {CalculoRotaViewModel(entregaRotaInteractor = get())}
//    viewModel {ListaEntregaRotaViewModel(entregaRotaInteractor = get()) }
//    viewModel {DadosEntregaRotaViewModel(entregaRotaInteractor = get()) }
//    viewModel {CalculoSimplesViewModel(entregaSimplesInteractor = get())}
//    viewModel {ListaEntregaSimplesViewModel(entregaSimplesInteractor = get())}
}