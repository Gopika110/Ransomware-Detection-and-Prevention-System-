-- Insert default admin user (Password is 'admin123' BCrypt-hashed)
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$tR5VG1KujIvaErG7D0QXuOeVnWQYuPMrDjLGbXejVsBVtu0AD37Te', 'ADMIN');

-- Insert initial file logs
INSERT INTO file_logs (file_name, file_path, activity_type, file_size, entropy, is_suspicious) VALUES
('document1.docx', 'C:\\monitored_directory\\document1.docx', 'CREATE', 15420, 4.25, FALSE),
('photo1.jpg', 'C:\\monitored_directory\\photo1.jpg', 'CREATE', 245000, 7.82, FALSE),
('script.py', 'C:\\monitored_directory\\script.py', 'MODIFY', 1240, 3.41, FALSE),
('system_log.txt', 'C:\\monitored_directory\\system_log.txt', 'DELETE', 520, 2.15, FALSE),
('invoice_draft.pdf', 'C:\\monitored_directory\\invoice_draft.pdf', 'CREATE', 87400, 5.12, FALSE);

-- Insert initial alerts
INSERT INTO alerts (message, severity, is_read) VALUES
('System file monitoring service initialized.', 'INFO', FALSE),
('Scanner detected high entropy file: photo1.jpg (entropy: 7.82). Categorized as safe media.', 'INFO', FALSE),
('Directory watch service started monitoring ./monitored_directory', 'INFO', FALSE);

-- Insert initial threats
INSERT INTO threats (name, level, status, affected_files) VALUES
('Mock Ransomware Pattern Detector', 'LOW', 'RESOLVED', 'C:\\monitored_directory\\test_file.locked');

-- Insert initial reports
INSERT INTO reports (title, threats_count, alerts_count, logs_count, summary) VALUES
('System Security Report - Initial Baseline', 1, 3, 5, 'Initial baseline system health check report. Monitored directory was set up and verified. System status is safe with zero active threats.');
