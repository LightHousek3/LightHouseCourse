package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Video;
import db.DBContext;

/**
 * Data Access Object for Video entity. Handles database operations related to
 * Video entities.
 */
public class VideoDAO extends DBContext {

    /**
     * Get all videos for a specific course.
     *
     * @param courseId The course ID
     * @return List of videos for the course
     */
    public List<Video> getByCourseId(int courseId) {
        List<Video> videos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // Join with Lessons to get videos from the course
            String sql = "SELECT v.* FROM Videos v "
                    + "JOIN Lessons l ON v.LessonID = l.LessonID "
                    + "WHERE l.CourseID = ? ";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Video video = mapRow(rs);
                videos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return videos;
    }

    /**
     * Get a specific video by ID.
     *
     * @param videoId The video ID
     * @return The video object or null if not found
     */
    public Video getVideoById(int videoId) {
        Video video = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Videos WHERE VideoID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, videoId);
            rs = ps.executeQuery();

            if (rs.next()) {
                video = mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return video;
    }

    /**
     * Insert a new video.
     *
     * @param video The video to insert
     * @return The generated video ID or -1 if failed
     */
    public int insert(Video video) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int videoId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Videos (LessonID, Title, Description, VideoUrl, Duration) "
                    + "VALUES (?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, video.getLessonID());
            ps.setString(2, video.getTitle());
            ps.setString(3, video.getDescription());
            ps.setString(4, video.getVideoUrl());
            ps.setInt(5, video.getDuration());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    videoId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return videoId;
    }

    /**
     * Update an existing video.
     *
     * @param video The video to update
     * @return True if successful, false otherwise
     */
    public boolean update(Video video) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Videos SET LessonID = ?, Title = ?, Description = ?, "
                    + "VideoUrl = ?, Duration = ? "
                    + "WHERE VideoID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, video.getLessonID());
            ps.setString(2, video.getTitle());
            ps.setString(3, video.getDescription());
            ps.setString(4, video.getVideoUrl());
            ps.setInt(5, video.getDuration());
            ps.setInt(6, video.getVideoID());

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Delete a video by ID.
     *
     * @param videoId The video ID to delete
     * @return True if successful, false otherwise
     */
    public boolean delete(int videoId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "DELETE FROM Videos WHERE VideoID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, videoId);

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Get videos for a specific lesson.
     *
     * @param lessonId The lesson ID
     * @return List of videos for the lesson
     */
    public List<Video> getVideosByLessonId(int lessonId) {
        List<Video> videos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Videos WHERE LessonID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Video video = mapRow(rs);
                videos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return videos;
    }

    /**
     * Save a video - either insert new or update existing.
     *
     * @param video The video to save
     * @return True if successful, false otherwise
     */
    public boolean save(Video video) {
        if (video.getVideoID() > 0) {
            return update(video);
        } else {
            return insert(video) > 0;
        }
    }

    public void insertWithConnection(Connection conn, Video video) throws SQLException {
        String sql = "INSERT INTO Videos (LessonID, Title, Description, VideoUrl, Duration) VALUES (?, ?, ?, ?, ?)";

        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Setting the values for the prepared statement
            ps.setInt(1, video.getLessonID());
            ps.setString(2, video.getTitle());
            ps.setString(3, video.getDescription());
            ps.setString(4, video.getVideoUrl());
            ps.setInt(5, video.getDuration()); // Assuming duration is stored in seconds

            // Executing the update and getting the generated keys (i.e., the Video ID)
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        video.setVideoID(rs.getInt(1)); // Assign the generated Video ID
                    }
                }
            }
        }
    }

    /**
     * Maps a ResultSet row to a Video object.
     *
     * @param rs The ResultSet
     * @return A Video object
     * @throws SQLException If an SQL error occurs
     */
    private Video mapRow(ResultSet rs) throws SQLException {
        Video video = new Video();
        video.setVideoID(rs.getInt("VideoID"));
        video.setLessonID(rs.getInt("LessonID"));
        video.setTitle(rs.getString("Title"));
        video.setDescription(rs.getString("Description"));
        video.setVideoUrl(rs.getString("VideoUrl"));
        video.setDuration(rs.getInt("Duration"));
        return video;
    }
}
