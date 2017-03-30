(function() {
    'use strict';

    angular
        .module('kafkalensApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('kafkaevent', {
            parent: 'entity',
            url: '/kafkaevent?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Kafkaevents'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/kafkaevent/kafkaevents.html',
                    controller: 'KafkaeventController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('kafkaevent-detail', {
            parent: 'kafkaevent',
            url: '/kafkaevent/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Kafkaevent'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/kafkaevent/kafkaevent-detail.html',
                    controller: 'KafkaeventDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Kafkaevent', function($stateParams, Kafkaevent) {
                    return Kafkaevent.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'kafkaevent',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('kafkaevent-detail.edit', {
            parent: 'kafkaevent-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kafkaevent/kafkaevent-dialog.html',
                    controller: 'KafkaeventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Kafkaevent', function(Kafkaevent) {
                            return Kafkaevent.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('kafkaevent.new', {
            parent: 'kafkaevent',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kafkaevent/kafkaevent-dialog.html',
                    controller: 'KafkaeventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                kafkakey: null,
                                kpartition: null,
                                koffset: null,
                                body: null,
                                eventtime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('kafkaevent', null, { reload: 'kafkaevent' });
                }, function() {
                    $state.go('kafkaevent');
                });
            }]
        })
        .state('kafkaevent.edit', {
            parent: 'kafkaevent',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kafkaevent/kafkaevent-dialog.html',
                    controller: 'KafkaeventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Kafkaevent', function(Kafkaevent) {
                            return Kafkaevent.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('kafkaevent', null, { reload: 'kafkaevent' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('kafkaevent.delete', {
            parent: 'kafkaevent',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kafkaevent/kafkaevent-delete-dialog.html',
                    controller: 'KafkaeventDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Kafkaevent', function(Kafkaevent) {
                            return Kafkaevent.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('kafkaevent', null, { reload: 'kafkaevent' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
