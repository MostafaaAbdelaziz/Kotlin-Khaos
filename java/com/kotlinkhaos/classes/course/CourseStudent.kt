package com.kotlinkhaos.classes.course

import com.kotlinkhaos.classes.errors.CourseJoinError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserDetails
import com.kotlinkhaos.classes.user.viewmodel.UserViewModel


/**
 * Represents student-related functionalities within a course context.
 * This class handles operations such as a student joining a course.
 */
class CourseStudent private constructor() {
    companion object {

        /**
         * Allows a student to join a course. It updates the course details and the student's user details accordingly.
         */
        suspend fun joinCourse(
            userViewModel: UserViewModel,
            courseDetails: CourseDetails,
            user: User
        ) {
            if (user.getCourseId().isNotEmpty()) {
                throw CourseJoinError("User already enrolled in course")
            }
            courseDetails.studentIds.add(user.getUserId())
            val userDetails = UserDetails(courseDetails.id, user.getName(), user.getType())
            User.createUserDetails(user.getUserId(), userDetails)
            CourseInstructor.createCourseDetails(courseDetails)
            // Updates userDetails in userViewModel cache
            userViewModel.saveDetails(userDetails.courseId, userDetails.type)
        }
    }

}