# Releasing COOLDOWN

Releases are created entirely from the GitHub web interface.

1. Open the repository's **Releases** page.
2. Click **Draft a new release**.
3. Choose **Create new tag** and enter a version such as `v0.1.0`, targeting `main`.
4. Add release notes if desired.
5. Click **Publish release**.

Publishing the release triggers the `Release` workflow. It checks out the selected release tag, builds a versioned JAR such as `cooldown-0.1.0.jar`, and attaches that JAR to the GitHub Release automatically.

No local Git commands are required.
