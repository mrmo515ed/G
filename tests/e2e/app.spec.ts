import { test, expect } from '@playwright/test';

test.describe('Anime Black V7 Full System Verification', () => {
  test('Health endpoint should return healthy status and memory usage', async ({ request }) => {
    const response = await request.get('/api/health');
    expect(response.ok()).toBeTruthy();
    const data = await response.json();
    expect(data.status).toBe('healthy');
    expect(data.capabilities).toContain('gemini-3.8-flash');
    expect(data.memory.heapUsed).toBeDefined();
  });

  test('Admin metrics endpoint should return genuine live stats without fake values', async ({ request }) => {
    const response = await request.get('/api/admin/metrics');
    expect(response.ok()).toBeTruthy();
    const data = await response.json();
    expect(data.success).toBe(true);
    expect(data.server.heapUsageMB).toBeGreaterThan(0);
    expect(data.performance.totalRequestsMeasured).toBeGreaterThan(0);
    expect(data.performance.firestoreConnection).toBeUndefined(); // Fake status was removed
  });

  test('Anime search endpoint returns verified metadata from external providers or safe fallback', async ({ request }) => {
    const response = await request.get('/api/anime/search?q=Naruto');
    expect([200, 502]).toContain(response.status());
    const data = await response.json();
    if (response.status() === 200) {
      expect(data.success).toBe(true);
      expect(Array.isArray(data.items)).toBeTruthy();
      if (data.items.length > 0) {
        expect(data.items[0].title).toBeDefined();
      }
    }
  });

  test('Gemini search endpoint handles missing prompt with 400 validation error', async ({ request }) => {
    const response = await request.post('/api/gemini/search-agent', {
      data: { prompt: '' },
    });
    expect(response.status()).toBe(400);
    const data = await response.json();
    expect(data.success).toBe(false);
  });

  test('Frontend root serves index.html with Arabic RTL, meta tags, and secure headers', async ({ request }) => {
    const response = await request.get('/');
    expect(response.ok()).toBeTruthy();
    expect(response.status()).toBe(200);

    const headers = response.headers();
    expect(headers['x-content-type-options']).toBe('nosniff');
    expect(headers['x-frame-options']).toBe('SAMEORIGIN');

    const html = await response.text();
    expect(html).toContain('lang="ar" dir="rtl"');
    expect(html).toContain('<title>Anime Black — أنمي بلاك</title>');
    expect(html).toContain('name="description" content="شبكة الأوتاكو الشاملة — أنمي، مانجا، مجتمعات، ألعاب واربح، وقصص في تطبيق واحد"');
    expect(html).toContain('name="viewport"');
    expect(html).toContain('/dist/anime-black-core.umd.cjs');
    expect(html).toContain('/rtm.js');
    expect(html).toContain('manifest.json');
  });

  test('All core production assets, bundles, and PWA files are served successfully', async ({ request }) => {
    const coreBundle = await request.get('/dist/anime-black-core.umd.cjs');
    expect(coreBundle.ok()).toBeTruthy();
    expect(coreBundle.status()).toBe(200);

    const rtm = await request.get('/rtm.js');
    expect(rtm.ok()).toBeTruthy();
    expect(rtm.status()).toBe(200);

    const manifest = await request.get('/manifest.json');
    expect(manifest.ok()).toBeTruthy();
    const manifestJson = await manifest.json();
    expect(manifestJson.name).toBe('Anime Black — أنمي بلاك');
    expect(manifestJson.start_url).toBe('/?source=pwa');

    const sw = await request.get('/sw.js');
    expect(sw.ok()).toBeTruthy();
    expect(sw.status()).toBe(200);

    const iconSvg = await request.get('/icon.svg');
    expect(iconSvg.ok()).toBeTruthy();
    expect(iconSvg.status()).toBe(200);
  });

  test('SPA routing fallback correctly serves index.html for direct sub-routes', async ({ request }) => {
    const deepRoute = await request.get('/worlds/anime-zone');
    expect(deepRoute.ok()).toBeTruthy();
    expect(deepRoute.status()).toBe(200);
    const html = await deepRoute.text();
    expect(html).toContain('<title>Anime Black — أنمي بلاك</title>');
  });
});


