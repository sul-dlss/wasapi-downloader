set :application, 'wasapi-downloader'
set :repo_url, "https://github.com/sul-dlss/#{fetch(:application)}.git"
set :user, 'was'

# Default branch is :master
ask :branch, `git rev-parse --abbrev-ref HEAD`.chomp

# Default deploy_to directory is /var/www/my_app_name
set :deploy_to, "/opt/app/#{fetch(:user)}/#{fetch(:application)}"

# Default value for :format is :airbrussh.
# set :format, :airbrussh

# You can configure the Airbrussh format using :format_options.
# These are the defaults.
# set :format_options, command_output: true, log_file: "log/capistrano.log", color: :auto, truncate: :auto

# Default value for :log_level is :debug
# set :log_level, :info

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# append :linked_files, 'config/settings.yml'

# Default value for linked_dirs is []
set :linked_dirs, %w{build .gradle}

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for keep_releases is 5
# set :keep_releases, 5

# update shared_configs before restarting app
# before 'deploy:restart', 'shared_configs:update'

namespace :gradle do
  desc 'Assemble a jar archive containing the main classes.'
  task :jar do
    on roles(:app) do
      execute "cd #{current_path} && ./gradlew jar"
    end
  end
end
after 'deploy:finished', 'gradle:jar'
