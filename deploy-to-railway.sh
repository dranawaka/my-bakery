#!/bin/bash

echo "🚀 Preparing My Bakery for Railway Deployment..."

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "❌ Git repository not found. Please initialize git first:"
    echo "   git init"
    echo "   git add ."
    echo "   git commit -m 'Initial commit'"
    exit 1
fi

# Check if remote origin exists
if ! git remote get-url origin > /dev/null 2>&1; then
    echo "❌ No remote origin found. Please add your GitHub repository:"
    echo "   git remote add origin https://github.com/yourusername/my-bakery.git"
    exit 1
fi

# Build the application locally to check for issues
echo "🔨 Building application..."
if ! mvn clean package -DskipTests; then
    echo "❌ Build failed. Please fix the issues before deploying."
    exit 1
fi

echo "✅ Build successful!"

# Check if Railway CLI is installed
if command -v railway &> /dev/null; then
    echo "🚂 Railway CLI found. You can deploy using:"
    echo "   railway login"
    echo "   railway up"
else
    echo "📝 Railway CLI not found. You can install it with:"
    echo "   npm install -g @railway/cli"
    echo ""
    echo "Or deploy directly from Railway dashboard:"
    echo "1. Go to https://railway.app"
    echo "2. Click 'New Project'"
    echo "3. Select 'Deploy from GitHub repo'"
    echo "4. Choose your repository"
fi

echo ""
echo "📋 Next steps:"
echo "1. Push your code to GitHub:"
echo "   git push origin main"
echo ""
echo "2. Set up environment variables in Railway dashboard"
echo "3. Add PostgreSQL database in Railway"
echo "4. Deploy and monitor the build logs"
echo ""
echo "📖 See RAILWAY_DEPLOYMENT.md for detailed instructions"
