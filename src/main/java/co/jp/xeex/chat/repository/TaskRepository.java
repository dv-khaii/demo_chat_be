package co.jp.xeex.chat.repository;

import co.jp.xeex.chat.base.RepositoryBase;
import co.jp.xeex.chat.domains.taskmngr.task.get.TaskFilter;
import co.jp.xeex.chat.entity.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * The repository for Task management.
 * 
 * @author q_thinh
 */
@Repository
public interface TaskRepository extends RepositoryBase<Task, String> {
    /**
     * Define the SQL query to retrieve tasks based on the provided parameters.
     */
    static final String GET_TASK_SQL = "" +
            " SELECT " +
            "   t1.* " +
            " FROM " +
            "   task t1, " +
            "   chat_group t2, " +
            "   dfw_m050m t3 " +
            " WHERE " +
            "   ( t1.createby = t3.emp_cd OR t1.assignee = t3.emp_cd ) " +
            "   AND ( t1.group_id = t2.id " +
            "         OR ( t1.group_id IS NULL AND ( :empCd IN ( t1.createby, t1.assignee ) ) ) " +
            "       ) " +
            "   AND ( t1.group_id IN :groupIds OR t1.group_id IS NULL ) " +
            "   AND ( :groupId = '' OR t1.group_id = :groupId ) " +
            // Start - filter by searchText
            "   AND ( :searchText = '' " +
            "         OR t1.task_name LIKE %:searchText% " +
            "         OR ( t1.group_id IS NOT NULL AND t2.group_name LIKE %:searchText% ) " +
            "         OR t1.assignee LIKE %:searchText% " +
            "         OR t1.createby LIKE %:searchText% " +
            "         OR t3.full_name LIKE %:searchText% " +
            "         OR t1.description LIKE %:searchText% " +
            "       )  " +
            // End - filter by searchText
            // Start - filter by taskFilter
            "   AND ( :#{#taskFilter.status} IS NULL OR t1.task_status = :#{#taskFilter.status} ) " +
            "   AND ( :#{#taskFilter.assigner} IS NULL OR t1.createBy = :#{#taskFilter.assigner} ) " +
            "   AND ( :#{#taskFilter.assignee} IS NULL OR t1.assignee = :#{#taskFilter.assignee} ) " +
            "   AND ( COALESCE(:#{#taskFilter.priorityList}) IS NULL " +
            "         OR t1.task_priority IN :#{#taskFilter.priorityList} " +
            "       ) " +
            "   AND ( :#{#taskFilter.dueDate} IS NULL " +
            "         OR t1.due_date IS NULL " +
            "         OR t1.due_date >= :#{#taskFilter.dueDate} " +
            "       ) " +
            "   AND ( :#{#taskFilter.overDueDate} IS NULL " +
            "         OR ( t1.due_date < :#{#taskFilter.overDueDate} AND t1.task_status <> 'DONE' ) " +
            "       ) " +
            // End - filter by taskFilter
            " GROUP BY t1.id " +
            " ORDER BY t1.createat DESC ";

    /**
     * Retrieves a page of tasks based on the provided parameters.
     *
     * @param groupIds   The list of group IDs to filter the tasks by.
     * @param groupId    The ID of the group to filter the tasks by.
     * @param empCd      The employee code used for filtering tasks.
     * @param searchText The search text used for filtering tasks.
     * @param taskFilter The task filter object used for additional filtering.
     * @param page       The page request object specifying the page number and
     *                   size.
     * @return A page of tasks that match the provided parameters.
     */
    @Query(value = "SELECT t.* FROM( " + GET_TASK_SQL + ") t", countQuery = "SELECT COUNT(*) " +
            " FROM (" + GET_TASK_SQL + ") taskCount", nativeQuery = true)
    Page<Task> getTasksByValue(@Param("groupIds") List<String> groupIds,
            @Param("groupId") String groupId,
            @Param("empCd") String empCd,
            @Param("searchText") String searchText,
            @Param("taskFilter") TaskFilter taskFilter,
            PageRequest page);
}
