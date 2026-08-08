package com.zidio.keystone.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.ActivityResponse;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.repository.WorkOrderRepository;



@RestController
@RequestMapping("/api")
public class DashboardController {



    private final UserRepository userRepository;

    private final WorkOrderRepository workOrderRepository;




    public DashboardController(

            UserRepository userRepository,

            WorkOrderRepository workOrderRepository

    ) {

        this.userRepository = userRepository;

        this.workOrderRepository = workOrderRepository;

    }





    // ===============================
    // DASHBOARD STATISTICS
    // ===============================


    @GetMapping("/dashboard")
    public Map<String, Long> getDashboardStats() {



        Map<String, Long> stats = new HashMap<>();



        // Total Orders

        stats.put(

                "totalOrders",

                workOrderRepository.count()

        );




        // Pending Orders

        long pendingOrders =

                workOrderRepository.countByStatusIn(

                        List.of(

                                "OPEN",

                                "IN_PROGRESS"

                        )

                );



        stats.put(

                "pendingOrders",

                pendingOrders

        );





        // Completed Orders

        long completedOrders =

                workOrderRepository.countByStatusIn(

                        List.of(

                                "COMPLETED",

                                "CLOSED"

                        )

                );



        stats.put(

                "completedOrders",

                completedOrders

        );





        // Users

        stats.put(

                "users",

                userRepository.count()

        );



        return stats;

    }






    // ===============================
    // RECENT ACTIVITY
    // ===============================


    @GetMapping("/dashboard/activity")
    public List<ActivityResponse> getRecentActivity(){



        List<WorkOrder> orders =

                workOrderRepository.findAll();



        List<ActivityResponse> activities =

                new ArrayList<>();




        orders.stream()

                .limit(5)

                .forEach(order -> {



                    activities.add(

                            new ActivityResponse(

                                    "Work Order "

                                    + order.getWorkOrderCode()

                                    + " created",

                                    order.getCreatedAt()

                            )

                    );


                });




        return activities;

    }



}