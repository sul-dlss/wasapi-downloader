# stage specific deployment info goes here
set :deploy_host, "was-downloader-stage.stanford.edu"
server fetch(:deploy_host), user: fetch(:user), roles: 'app'

# allow ssh to host
Capistrano::OneTimeKey.generate_one_time_key!
